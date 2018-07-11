package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.ObservableBoolean
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.view.*
import com.droidfeed.R
import com.droidfeed.data.model.Article
import com.droidfeed.databinding.FragmentArticlesBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.DataInsertedCallback
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.util.AppRateHelper
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extention.isOnline
import com.droidfeed.util.shareCount
import javax.inject.Inject


/**
 * Fragment responsible for news feed.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
class FeedFragment : BaseFragment() {

    companion object {
        private const val REQUEST_CODE_SHARE = 4122
        private const val EXTRA_FEED_TYPE = "feed_type"

        fun getInstance(feedType: FeedType): FeedFragment {
            val feedFragment = FeedFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_FEED_TYPE, feedType.name)
            feedFragment.arguments = bundle
            return feedFragment
        }
    }

    private val isEmptyFeed = ObservableBoolean(false)
    private val isEmptyBookmarked = ObservableBoolean(false)

    private lateinit var binding: FragmentArticlesBinding
    private lateinit var adapter: UiModelAdapter
    private var viewModel: FeedViewModel? = null
    private val feedType by lazy {
        arguments?.getString(EXTRA_FEED_TYPE)?.let { FeedType.valueOf(it) }
    }

    private val dataInsertedCallback = object : DataInsertedCallback {
        override fun onUpdated() {
            if (binding.swipeRefreshArticles.isRefreshing) {
                binding.swipeRefreshArticles.isRefreshing = false
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var customTab: CustomTab

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var appRateHelper: AppRateHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (feedType == FeedType.ALL) setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        if (viewModel == null) {
            viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(FeedViewModel::class.java)

            feedType?.let { viewModel!!.setFeedType(it) }
        }

        init()
        initDataObservables()
    }

    private fun init() {
        val layoutManager = activity?.let { WrapContentLinearLayoutManager(it) }
        adapter = UiModelAdapter(dataInsertedCallback, layoutManager)

        binding.apply {
            newsRecyclerView.layoutManager = layoutManager

            (newsRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            newsRecyclerView.swapAdapter(adapter, true)
            swipeRefreshArticles.setOnRefreshListener { this@FeedFragment.viewModel?.onRefreshArticles() }

            viewModel = this@FeedFragment.viewModel
            isEmptyBookmarked = isEmptyBookmarked
            isEmptyFeed = isEmptyFeed
        }
    }

    private fun initDataObservables() {
        viewModel?.apply {

            noSourceSelected.observe(this@FeedFragment, Observer {
                it?.let { isEmptyFeed.set(it) }
            })

            noBookmarkedArticle.observe(this@FeedFragment, Observer {
                it?.let { isEmptyBookmarked.set(it) }
            })

            rssUiModelData.observe(this@FeedFragment, Observer {
                adapter.addUiModels(it as Collection<BaseUiModelAlias>)
            })

            articleOpenDetail.observe(this@FeedFragment, Observer {
                it?.let { customTab.showTab(it.link) }
            })

            articleShareEvent.observe(this@FeedFragment, Observer {
                sharedPrefs.shareCount += 1
                startActivityForResult(it, REQUEST_CODE_SHARE)
            })

            articleBookmarkEvent.observe(this@FeedFragment, Observer {
                appRateHelper.checkAppRatePrompt(binding.root)
            })

            loadingFailedEvent.observe(this@FeedFragment, Observer {
                showLoadFailedSnackbar(it)
            })

            if (feedType == FeedType.BOOKMARKS) {
                articleOnUnBookmark.observe(this@FeedFragment, Observer { article ->
                    showBookmarkUndoSnackbar(article)
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SHARE -> {
                appRateHelper.checkAppRatePrompt(binding.root)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.activity_main_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showBookmarkUndoSnackbar(article: Article?) {
        article?.let {
            Snackbar.make(
                binding.root,
                R.string.info_bookmark_removed,
                Snackbar.LENGTH_LONG
            )
                .setActionTextColor(Color.YELLOW)
                .setAction(R.string.undo) {
                    viewModel!!.toggleBookmark(article)
                }

                .show()
        }
    }

    private fun showLoadFailedSnackbar(it: Boolean?) {
        if (it == true) {
            val snackBarText = if (activity?.isOnline() == true) {
                getString(R.string.error_obtaining_feed)
            } else {
                getString(R.string.info_no_internet) + " " +
                        getString(R.string.can_not_refresh)
            }

            Snackbar.make(
                binding.root,
                snackBarText,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    internal fun scrollToTop() {
        binding.newsRecyclerView.smoothScrollToPosition(0)
    }

}
