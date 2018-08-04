package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.R
import com.droidfeed.databinding.FragmentArticlesBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.DataInsertedCallback
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.util.AnalyticsUtil
import com.droidfeed.util.AppRateHelper
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extention.isOnline
import com.droidfeed.util.shareCount
import javax.inject.Inject

/**
 * Base feed fragment responsible displaying articles.
 * @see [BookmarksFragment], [NewsFeedFragment]
 */
open class FeedFragment : BaseFragment() {

    protected lateinit var viewModel: FeedViewModel
    protected lateinit var binding: FragmentArticlesBinding
    private lateinit var adapter: UiModelAdapter

    private val dataInsertedCallback = object : DataInsertedCallback {
        override fun onUpdated() {
            if (binding.swipeRefreshArticles.isRefreshing) {
                binding.swipeRefreshArticles.isRefreshing = false
            }
        }
    }

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
        binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!::viewModel.isInitialized) {
            viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(FeedViewModel::class.java)
        }

        init()
        initDataObservables()
    }

    private fun init() {
        val layoutManager = activity?.let { WrapContentLinearLayoutManager(it) }

        if (!::adapter.isInitialized) {
            adapter = UiModelAdapter(dataInsertedCallback, layoutManager)
        }

        binding.apply {
            newsRecyclerView.layoutManager = layoutManager

            (newsRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            newsRecyclerView.swapAdapter(adapter, true)
            swipeRefreshArticles.setOnRefreshListener { this@FeedFragment.viewModel.onRefreshArticles() }

            viewModel = this@FeedFragment.viewModel
        }
    }

    private fun initDataObservables() {
        viewModel.apply {
            rssUiModelData.observe(this@FeedFragment, Observer {
                adapter.addUiModels(it as Collection<BaseUiModelAlias>)
            })

            articleOpenDetail.observe(this@FeedFragment, Observer {
                it?.let {
                    customTab.showTab(it.link)
                    analytics.logArticleClick()
                }
            })

            articleShareEvent.observe(this@FeedFragment, Observer {
                sharedPrefs.shareCount += 1
                startActivityForResult(it, REQUEST_CODE_SHARE)
                analytics.logShare()
            })

            articleBookmarkEvent.observe(this@FeedFragment, Observer {
                appRateHelper.checkAppRatePrompt(binding.root)
            })

            loadingFailedEvent.observe(this@FeedFragment, Observer {
                showLoadFailedSnackbar(it)
            })
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
