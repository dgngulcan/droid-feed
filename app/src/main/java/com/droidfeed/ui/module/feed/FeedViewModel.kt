package com.droidfeed.ui.module.feed

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import com.droidfeed.R
import com.droidfeed.data.model.Post
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SharedPrefsRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.module.feed.analytics.FeedScreenLogger
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.event.Event
import com.droidfeed.util.extension.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST", "WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class FeedViewModel @Inject constructor(
    private val sourceRepo: SourceRepo,
    private val postRepo: PostRepo,
    private val sharedPrefs: SharedPrefsRepo,
    private val logger: FeedScreenLogger,
    private val appRateInteractor: AppRateInteractor
) : ViewModel() {

    private val feedType = MutableLiveData<FeedType>()
    private lateinit var refreshJob: Job

    val postsLiveData: LiveData<PagedList<PostUIModel>> = switchMap(feedType) { type ->
        when (type) {
            FeedType.POSTS -> {
                postRepo.getAll()
                    .asLiveData(pagedListConfig) { posts ->
                        createPostUIModel(posts)
                    }
            }
            FeedType.BOOKMARKS -> {
                postRepo.getBookmarked()
                    .asLiveData(pagedListConfig) { posts ->
                        createPostUIModel(posts)
                    }
            }
        }
    }

    val isEmptyStateVisible: LiveData<Boolean> = Transformations.map(postsLiveData) { list ->
        if (list.isEmpty() && !refreshJob.isActive) {
            when (feedType.value) {
                FeedType.BOOKMARKS -> {
                    emptyStateDrawable.postValue(R.drawable.ic_empty_state_bookmark)
                    emptyTitleText.postValue(R.string.empty_bookmark_title)
                    emptySubtitleText.postValue(R.string.empty_string)
                }
                FeedType.POSTS -> {
                    emptyStateDrawable.postValue(R.drawable.ic_empty_state_bookshelf)
                    emptyTitleText.postValue(R.string.empty_source_title)
                    emptySubtitleText.postValue(R.string.empty_source_content)
                }
            }
            true
        } else {
            false
        }
    }

    val emptyStateDrawable = MutableLiveData<@DrawableRes Int>()
        .apply {
            value = R.drawable.ic_df_logo
        }
    val emptyTitleText = MutableLiveData<@StringRes Int>()
        .apply {
            value = R.string.empty_string
        }
    val emptySubtitleText = MutableLiveData<@StringRes Int>()
        .apply {
            value = R.string.empty_string
        }

    val isProgressVisible = MutableLiveData<Boolean>().apply { value = false }
    val isRefreshing = MutableLiveData<Boolean>().apply { value = false }
    val openPostDetail = MutableLiveData<Event<Post>>()
    val showUndoBookmarkSnack = MutableLiveData<Event<() -> Unit>>()
    val showAppRateSnack = MutableLiveData<Event<() -> Unit>>()
    val sharePost = MutableLiveData<Event<Post>>()
    val intentToStart = MutableLiveData<Event<IntentProvider.TYPE>>()

    init {
        refreshJob = refresh()
    }

    private val pagedListConfig = PagedList.Config.Builder()
        .setPageSize(ITEM_PER_PAGE)
        .setEnablePlaceholders(false)
        .build()

    private fun createPostUIModel(posts: List<Post>): List<PostUIModel> {
        /* mark first item of every page as large */
        if (posts.isNotEmpty()) {
            posts[0].layoutType = UIModelType.POST_LARGE
        }

        return posts.map { PostUIModel(it, postClickCallback) }
    }

    private val postClickCallback =
        object : PostClickListener {
            override fun onItemClick(post: Post) {
                openPostDetail.postValue(Event(post))
                logger.logPostClick()
            }

            override fun onShareClick(post: Post) {
                sharePost.postValue(Event(post))
                sharedPrefs.incrementItemShareCount()
                logger.logPostShare()
            }

            override fun onBookmarkClick(post: Post) {
                togglePostBookmark(post)
            }
        }

    fun setFeedType(feedType: FeedType) {
        if (this.feedType.value != feedType) {
            this.feedType.value = feedType
        }
    }

    fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        if (postsLiveData.value?.isEmpty() == true) {
            isProgressVisible.postValue(true)
        }

        postRepo.refresh(
            this,
            sourceRepo.getActives()
        )

        isProgressVisible.postValue(false)
        isRefreshing.postValue(false)
    }

    fun togglePostBookmark(post: Post) {
        if (post.isBookmarked()) {
            post.bookmarked = 0

            if (feedType.value == FeedType.BOOKMARKS) {
                showUndoBookmarkSnack.postValue(Event {
                    togglePostBookmark(post)
                })
            }
        } else {
            post.bookmarked = 1
            tryShowingAppRateSnackbar()
        }

        logger.logBookmark(post.bookmarked == 1)

        viewModelScope.launch(Dispatchers.IO) {
            postRepo.updatePost(post)
        }
    }

    fun onReturnedFromPost() {
        tryShowingAppRateSnackbar()
    }

    private fun tryShowingAppRateSnackbar() {
        if (appRateInteractor.canShowAppRate()) {
            viewModelScope.launch(Dispatchers.IO) {
                val bookmarkCount = postRepo.getBookmarkedCount()

                if (appRateInteractor.isFitForAppRatePrompt(bookmarkCount)) {
                    logger.logAppRatePrompt()

                    showAppRateSnack.postValue(Event {
                        intentToStart.postValue(Event(IntentProvider.TYPE.RATE_APP))
                        logger.logAppRateFromPromtClick()
                    })
                }
            }
        }
    }


    companion object {
        private const val ITEM_PER_PAGE = 20
    }
}