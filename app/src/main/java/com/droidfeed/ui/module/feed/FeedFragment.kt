package com.droidfeed.ui.module.feed

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Post
import com.droidfeed.databinding.FragmentFeedBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelPaginatedAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.util.AppRateHelper
import com.droidfeed.util.CustomTab
import com.droidfeed.util.event.EventObserver
import com.droidfeed.util.extention.isOnline
import com.droidfeed.util.shareCount
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

class FeedFragment : BaseFragment("feed") {

    private lateinit var viewModel: FeedViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentFeedBinding

    private val adapter by lazy { UIModelPaginatedAdapter(this) }

    @Inject
    lateinit var customTab: CustomTab

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var appRateHelper: AppRateHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders
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
        binding = FragmentFeedBinding.inflate(inflater, container, false)


        subscribeNetworkState()
        subscribePosts()
        subscribePostBookmarkEvent()
        subscribePostOpenEvent()
        subscribePostShareEvent()
        subscribeActiveSourceCount()
        subscribeBookmarksOpenEvent()
        subscribeUnBookmarkEvent()

        viewModel.setFeedType(FeedType.POSTS)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.apply {
            val layoutManager = activity?.let { WrapContentLinearLayoutManager(it) }
            newsRecyclerView.layoutManager = layoutManager

            var scrolledAmount = 0

            newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    scrolledAmount += dy.absoluteValue

                    if (scrolledAmount > MENU_SCROLL_THRESHOLD_TO_CLOSE) {
                        mainViewModel.onScrolledEnough()
                        scrolledAmount = 0
                    }

                    GlobalScope.launch {
                        delay(MENU_SCROLL_THRESHOLD_TO_CLOSE)
                        scrolledAmount = 0
                    }
                }
            })

            (newsRecyclerView.itemAnimator as androidx.recyclerview.widget.DefaultItemAnimator)
                .supportsChangeAnimations = false
            newsRecyclerView.swapAdapter(adapter, true)

            swipeRefreshArticles.setOnRefreshListener {
                if (context?.isOnline() == true) {
                    viewModel.refresh()
                } else {
                    swipeRefreshArticles.isRefreshing = false
                    Snackbar.make(
                        swipeRefreshArticles,
                        R.string.info_no_internet,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun subscribeNetworkState() {
        viewModel.networkState.observe(viewLifecycleOwner, Observer { dataStatus ->
            when (dataStatus) {
                is DataStatus.Loading -> {
                    if (adapter.isEmpty()) {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
                is DataStatus.Successful,
                is DataStatus.Failed,
                is DataStatus.HttpFailed -> {
                    if (swipeRefreshArticles.isRefreshing) {
                        swipeRefreshArticles.isRefreshing = false
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun subscribePosts() {
        viewModel.postsLiveData.observe(this, Observer { pagedList ->
            binding.progressBar.isVisible = false

            adapter.submitList(pagedList as PagedList<BaseUIModelAlias>)

            binding.containerEmptyBookmark.visibility =
                    if (pagedList.isEmpty() && !swipeRefreshArticles.isEnabled) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
        })
    }

    private fun subscribePostBookmarkEvent() {
        viewModel.postBookmarkEvent.observe(viewLifecycleOwner, EventObserver {
            appRateHelper.checkAppRatePrompt(binding.root)
            analytics.logBookmark(true)
        })
    }

    private fun subscribePostOpenEvent() {
        viewModel.postOpenDetail.observe(viewLifecycleOwner, EventObserver { post ->
            customTab.showTab(post.link)
            analytics.logPostClick()
        })
    }

    private fun subscribePostShareEvent() {
        viewModel.postShareEvent.observe(viewLifecycleOwner, EventObserver { post ->
            sharedPrefs.shareCount += 1
            startActivityForResult(post.getShareIntent(), REQUEST_CODE_SHARE)
            analytics.logShare("post")
        })
    }

    private fun subscribeUnBookmarkEvent() {
        viewModel.postUnBookmarkEvent.observe(viewLifecycleOwner, EventObserver { post ->
            if (viewModel.feedType.value == FeedType.BOOKMARKS) {
                showBookmarkUndoSnackbar(post)
            }
            analytics.logBookmark(false)
        })
    }

    private fun subscribeActiveSourceCount() {
        viewModel.activeSourceCountLiveData.observe(this, Observer { count ->
            binding.containerEmptySource.isVisible = count == 0
        })
    }

    private fun subscribeBookmarksOpenEvent() {
        mainViewModel.bookmarksEvent.observe(viewLifecycleOwner, EventObserver { isEnabled ->
            val feedType = when {
                isEnabled -> {
                    FeedType.BOOKMARKS
                }
                else -> FeedType.POSTS
            }
            viewModel.setFeedType(feedType)
            swipeRefreshArticles.isEnabled = !isEnabled
        })
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
        private const val MENU_SCROLL_THRESHOLD_TO_CLOSE = 200L
    }
}
