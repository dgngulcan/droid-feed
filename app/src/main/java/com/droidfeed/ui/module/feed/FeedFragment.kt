package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.view.*
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Post
import com.droidfeed.databinding.FragmentFeedBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelPaginatedAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.util.AnalyticsUtil
import com.droidfeed.util.AppRateHelper
import com.droidfeed.util.CustomTab
import com.droidfeed.util.shareCount
import kotlinx.android.synthetic.main.fragment_feed.*
import javax.inject.Inject

class FeedFragment : BaseFragment() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var binding: FragmentFeedBinding
    private lateinit var adapter: UiModelPaginatedAdapter

    @Inject
    lateinit var customTab: CustomTab

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var appRateHelper: AppRateHelper

    @Inject
    lateinit var analytics: AnalyticsUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(FeedViewModel::class.java)
        }

        init()
        initDataObservables()
    }

    private fun init() {
        viewModel.setFeedType(FeedType.POSTS)

        if (!::adapter.isInitialized) {
            adapter = UiModelPaginatedAdapter(R.layout.list_item_placeholder_post)
        }


        binding.apply {
            val layoutManager = activity?.let { WrapContentLinearLayoutManager(it) }
            newsRecyclerView.layoutManager = layoutManager

            (newsRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            newsRecyclerView.swapAdapter(adapter, true)

            swipeRefreshArticles.setOnRefreshListener {
                this@FeedFragment.viewModel.refresh()
            }
        }
    }

    private fun initDataObservables() {
        viewModel.posts.observe(this, Observer { pagedList ->
            pagedList?.let { list ->
                adapter.submitList(list as PagedList<BaseUiModelAlias>)
            }
        })

        viewModel.networkState.observe(this, Observer {
            when (it) {
                DataStatus.Success -> {
                    if (swipeRefreshArticles.isRefreshing) {
                        swipeRefreshArticles.isRefreshing = false
                    }
                }
                DataStatus.Loading -> {
                }
                DataStatus.Error<Any>() -> {
                    if (swipeRefreshArticles.isRefreshing) {
                        swipeRefreshArticles.isRefreshing = false
                    }
                }
            }
        })

        viewModel.articleBookmarkEvent.observe(this, Observer {
            appRateHelper.checkAppRatePrompt(binding.root)
        })

        viewModel.articleOpenDetail.observe(this, Observer {
            it?.let { post -> customTab.showTab(post.link) }
            analytics.logPostClick()
        })

        viewModel.articleShareEvent.observe(this, Observer {
            sharedPrefs.shareCount += 1
            startActivityForResult(it, REQUEST_CODE_SHARE)
            analytics.logPostShare()
        })

        viewModel.articleUnBookmarkEvent.observe(this, Observer { article ->
            article?.let {
                showBookmarkUndoSnackbar(it)
                analytics.logBookmark(it.bookmarked == 1)
            }
        })

        viewModel.sources.observe(this, Observer { sources ->
            val activeSource = sources?.firstOrNull { it.isActive }
            binding.txtEmptySource.visibility = if (activeSource != null) {
                View.GONE
            } else {
                View.VISIBLE
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.fragment_news_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_bookmarks -> {
                viewModel.setFeedType(FeedType.BOOKMARKS)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showBookmarkUndoSnackbar(post: Post) {
        Snackbar.make(
            binding.root,
            R.string.info_bookmark_removed,
            Snackbar.LENGTH_LONG
        )
            .setActionTextColor(Color.YELLOW)
            .setAction(R.string.undo) { viewModel.toggleBookmark(post) }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SHARE -> appRateHelper.checkAppRatePrompt(binding.root)
        }
    }

    internal fun scrollToTop() {
        binding.newsRecyclerView.smoothScrollToPosition(0)
    }

    companion object {
        private const val REQUEST_CODE_SHARE = 4122
    }
}
