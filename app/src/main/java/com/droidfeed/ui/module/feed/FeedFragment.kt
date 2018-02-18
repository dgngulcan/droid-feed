package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableBoolean
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.R
import com.droidfeed.data.model.Article
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.databinding.FragmentArticlesBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.DataInsertedCallback
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.util.CustomTab
import com.droidfeed.util.DebugUtils
import com.droidfeed.util.NetworkUtils
import com.droidfeed.util.extention.isOnline
import com.nytclient.ui.common.BaseFragment
import org.jetbrains.anko.design.snackbar
import javax.inject.Inject


/**
 * Fragment responsible for news feed.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
class FeedFragment : BaseFragment() {

    companion object {
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
    private var viewModel: FeedViewModel? = null
    private lateinit var adapter: UiModelAdapter

    @Inject lateinit var newsRepo: RssRepo
    @Inject lateinit var customTab: CustomTab
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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
        if (viewModel == null) {
            feedType?.let {
                val factory = FeedViewModel.Factory(newsRepo, it)
                viewModel = ViewModelProviders
                        .of(this, factory)
                        .get(FeedViewModel::class.java)
            }
        }

        binding.viewModel = viewModel
        binding.isEmptyBookmarked = isEmptyBookmarked

        initDataObservables()
    }

    private fun init() {
        val layoutManager = activity?.let { WrapContentLinearLayoutManager(it) }
        adapter = UiModelAdapter(dataInsertedCallback, layoutManager)

        binding.newsRecyclerView.layoutManager = layoutManager

        (binding.newsRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        binding.newsRecyclerView.swapAdapter(adapter, true)

        binding.swipeRefreshArticles.setOnRefreshListener {
            viewModel?.onRefreshArticles()
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
                startActivity(it)
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

    private fun showBookmarkUndoSnackbar(article: Article?) {
        article?.let {
            snackbar(binding.root, R.string.info_bookmark_removed, R.string.undo, {
                viewModel!!.toggleBookmark(article)
            }).setActionTextColor(Color.YELLOW)
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
            snackbar(binding.root, snackBarText)
        }
    }
    internal fun scrollToTop() {
        binding.newsRecyclerView.smoothScrollToPosition(0)
    }

}