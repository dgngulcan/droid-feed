package com.droidfeed.ui.module.feed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
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
import com.droidfeed.util.extension.isOnline
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_feed.*
import javax.inject.Inject


class FeedFragment : BaseFragment("feed"), Scrollable {

    @Inject lateinit var customTab: CustomTab
    @Inject lateinit var appRateHelper: AppRateHelper

    private val feedViewModel: FeedViewModel by viewModels { viewModelFactory }
    private val mainViewModel: MainViewModel by activityViewModels { viewModelFactory }
    private val paginatedAdapter by lazy { UIModelPaginatedAdapter(lifecycleScope) }
    private lateinit var binding: FragmentFeedBinding

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

    @Suppress("UNCHECKED_CAST")
    private fun subscribePosts() {
        feedViewModel.postsLiveData.observe(viewLifecycleOwner, Observer { pagedList ->
            paginatedAdapter.submitList(pagedList as PagedList<BaseUIModelAlias>)
        })
    }

    private fun subscribePlayStoreEvent() {
        feedViewModel.openPlayStorePage.observe(viewLifecycleOwner, EventObserver {
            startActivity(rateAppIntent)
        })
    }

    private fun initFeed() {
        binding.newsRecyclerView.apply {
            layoutManager = WrapContentLinearLayoutManager(requireContext())

            addOnScrollListener(CollapseScrollListener(lifecycleScope) {
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
                        com.droidfeed.R.string.info_no_internet,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
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
                com.droidfeed.R.string.info_bookmark_removed,
                Snackbar.LENGTH_LONG
            ).apply {
                setActionTextColor(Color.YELLOW)
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
                setAction(com.droidfeed.R.string.undo) { onUndo() }
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