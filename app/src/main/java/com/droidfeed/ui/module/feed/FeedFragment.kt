package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.databinding.FragmentFeedBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelPaginatedAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.util.AnalyticsUtil
import com.droidfeed.util.AppRateHelper
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extention.isOnline
import kotlinx.android.synthetic.main.fragment_feed.*
import javax.inject.Inject

/**
 * Base feed fragment responsible displaying articles.
 * @see [BookmarksFragment], [NewsFeedFragment]
 */
open class FeedFragment : BaseFragment() {

    protected lateinit var viewModel: FeedViewModel2
    protected lateinit var binding: FragmentFeedBinding
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

        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(FeedViewModel2::class.java)
        }

        init()
        initDataObservables()
    }

    private fun init() {
        val layoutManager = activity?.let { WrapContentLinearLayoutManager(it) }

        viewModel.setFeedType(FeedType.NEWS)

        if (!::adapter.isInitialized) {
            adapter = UiModelPaginatedAdapter(R.layout.list_item_placeholder_post)
        }

        binding.apply {
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
                // todo check if list is empty and reflect on view
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SHARE -> {
                appRateHelper.checkAppRatePrompt(binding.root)
            }
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

    companion object {
        private const val REQUEST_CODE_SHARE = 4122
    }
}
