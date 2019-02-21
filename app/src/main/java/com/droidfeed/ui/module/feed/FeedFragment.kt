package com.droidfeed.ui.module.feed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import com.droidfeed.R
import com.droidfeed.databinding.FragmentFeedBinding
import com.droidfeed.rateAppIntent
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelPaginatedAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.CollapseScrollListener
import com.droidfeed.ui.common.Scrollable
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.util.AppRateHelper
import com.droidfeed.util.CustomTab
import com.droidfeed.util.event.EventObserver
import com.droidfeed.util.extention.isOnline
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_feed.*
import javax.inject.Inject

class FeedFragment : BaseFragment("feed"), Scrollable {

    private lateinit var feedViewModel: FeedViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentFeedBinding

    private val paginatedAdapter by lazy { UIModelPaginatedAdapter(this) }

    @Inject
    lateinit var customTab: CustomTab

    @Inject
    lateinit var appRateHelper: AppRateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        feedViewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(FeedViewModel::class.java)

        mainViewModel = ViewModelProviders
            .of(activity!!)
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            viewModel = feedViewModel
            lifecycleOwner = this@FeedFragment
        }

        subscribePosts()
        subscribeAppRateEvent()
        subscribeIsRefreshing()
        subscribePostOpenEvent()
        subscribePostShareEvent()
        subscribePlayStoreEvent()
        subscribeUnBookmarkEvent()
        subscribeBookmarksOpenEvent()

        feedViewModel.setFeedType(FeedType.POSTS)

        initFeed()
        initSwipeRefresh()

        return binding.root
    }

    private fun subscribePlayStoreEvent() {
        feedViewModel.openPlayStorePage.observe(viewLifecycleOwner, EventObserver {
            startActivity(rateAppIntent)
        })
    }

    private fun initFeed() {
        binding.newsRecyclerView.apply {
            layoutManager = WrapContentLinearLayoutManager(requireContext())

            addOnScrollListener(CollapseScrollListener(this@FeedFragment) {
                mainViewModel.onCollapseMenu()
            })

            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

            swapAdapter(
                paginatedAdapter,
                false
            )
        }
    }

    private fun subscribeAppRateEvent() {
        feedViewModel.showAppRateSnack.observe(viewLifecycleOwner, EventObserver { onAction ->
            appRateHelper.showRateSnackbar(binding.root) {
                onAction()
            }
        })
    }

    private fun subscribeIsRefreshing() {
        feedViewModel.isRefreshing.observe(viewLifecycleOwner, Observer { isRefreshing ->
            swipeRefreshPosts.isRefreshing = isRefreshing
        })
    }

    private fun initSwipeRefresh() {
        binding.swipeRefreshPosts.setOnRefreshListener {
            when {
                context?.isOnline() == true -> feedViewModel.refresh()
                else -> {
                    binding.swipeRefreshPosts.isRefreshing = false
                    Snackbar.make(
                        binding.root,
                        R.string.info_no_internet,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun subscribePosts() {
        feedViewModel.postsLiveData.observe(viewLifecycleOwner, Observer { pagedList ->
            paginatedAdapter.submitList(pagedList as PagedList<BaseUIModelAlias>)
        })
    }

    private fun subscribePostOpenEvent() {
        feedViewModel.openPostDetail.observe(viewLifecycleOwner, EventObserver { post ->
            customTab.showTab(post.link)
        })
    }

    private fun subscribePostShareEvent() {
        feedViewModel.sharePost.observe(viewLifecycleOwner, EventObserver { post ->
            startActivityForResult(post.getShareIntent(), REQUEST_CODE_SHARE)
        })
    }

    private fun subscribeUnBookmarkEvent() {
        feedViewModel.showUndoBookmarkSnack.observe(viewLifecycleOwner, EventObserver { onUndo ->
            Snackbar.make(
                binding.root,
                R.string.info_bookmark_removed,
                Snackbar.LENGTH_LONG
            ).apply {
                setActionTextColor(Color.YELLOW)
                setAction(R.string.undo) { onUndo() }
            }.run {
                show()
            }
        })
    }

    private fun subscribeBookmarksOpenEvent() {
        mainViewModel.isBookmarksShown.observe(viewLifecycleOwner, Observer { isEnabled ->
            val feedType = if (isEnabled) FeedType.BOOKMARKS else FeedType.POSTS
            feedViewModel.setFeedType(feedType)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SHARE -> feedViewModel.onReturnedFromPost()
        }
    }

    override fun scrollToTop() {
        binding.newsRecyclerView.smoothScrollToPosition(0)
    }

    companion object {
        private const val REQUEST_CODE_SHARE = 4122
    }
}