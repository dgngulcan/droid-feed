package com.droidfeed.ui.module.feed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import com.droidfeed.databinding.FragmentFeedBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelPaginatedAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.CollapseScrollListener
import com.droidfeed.ui.common.Scrollable
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.util.AppRateHelper
import com.droidfeed.util.CustomTab
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.extension.isOnline
import com.droidfeed.util.extension.observeEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_feed.*
import javax.inject.Inject


@AndroidEntryPoint
class FeedFragment : BaseFragment(), Scrollable {

    @Inject lateinit var customTab: CustomTab
    @Inject lateinit var appRateHelper: AppRateHelper
    @Inject lateinit var intentProvider: IntentProvider
    @Inject lateinit var paginatedAdapter: UIModelPaginatedAdapter

    private val feedViewModel: FeedViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
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
        subscribeFeedType()

        initFeed()
        initSwipeRefresh()

        return binding.root
    }

    private fun subscribeFeedType() {
        mainViewModel.isDisplayingBookmarkedItems.observe(viewLifecycleOwner) { isDisplaying ->
            feedViewModel.isDisplayingBookmarkedItems(isDisplaying)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun subscribePosts() {
        feedViewModel.postsLiveData.observe(viewLifecycleOwner) { pagedList ->
            paginatedAdapter.submitList(pagedList as PagedList<BaseUIModelAlias>)
        }
    }

    private fun subscribePlayStoreEvent() {
        feedViewModel.intentToStart.observeEvent(viewLifecycleOwner) { intentType ->
            startActivity(intentProvider.getIntent(intentType))
        }
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
        feedViewModel.showAppRateSnack.observeEvent(viewLifecycleOwner) { onAction ->
            appRateHelper.showRateSnackbar(binding.root) {
                onAction()
            }
        }
    }

    private fun subscribeIsRefreshing() {
        feedViewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            swipeRefreshPosts.isRefreshing = isRefreshing
        }
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
        feedViewModel.openPostDetail.observeEvent(viewLifecycleOwner) { post ->
            customTab.showTab(post.link)
        }
    }

    private fun subscribePostShareEvent() {
        feedViewModel.sharePost.observeEvent(viewLifecycleOwner) { post ->
            startActivityForResult(post.getShareIntent(), REQUEST_CODE_SHARE)
        }
    }

    private fun subscribeUnBookmarkEvent() {
        feedViewModel.showUndoBookmarkSnack.observeEvent(viewLifecycleOwner) { onUndo ->
            Snackbar.make(
                binding.root,
                com.droidfeed.R.string.info_bookmark_removed,
                Snackbar.LENGTH_LONG
            ).apply {
                setActionTextColor(Color.YELLOW)
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
                setAction(com.droidfeed.R.string.undo) { onUndo() }
            }.also {
                it.show()
            }
        }
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