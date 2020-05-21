package com.droidfeed.ui.module.feed

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.droidfeed.R
import com.droidfeed.data.model.Post
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SharedPrefsRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.event.Event
import com.droidfeed.util.extension.asLiveData
import com.droidfeed.util.extension.postEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST", "WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class FeedViewModel @Inject constructor(
    private val sourceRepo: SourceRepo,
    private val postRepo: PostRepo,
    private val sharedPrefs: SharedPrefsRepo,
    private val appRateInteractor: AppRateInteractor,
    mainViewModel: MainViewModel
) : ViewModel() {

    private lateinit var refreshJob: Job

    val emptyStateDrawable = MutableLiveData(R.drawable.ic_df_logo)
    val emptyTitle = MutableLiveData(R.string.empty_string)
    val emptySubtitle = MutableLiveData(R.string.empty_string)
    val isProgressVisible = MutableLiveData(false)
    val isRefreshing = MutableLiveData(false)
    val openPostDetail = MutableLiveData<Event<Post>>()
    val showUndoBookmarkSnack = MutableLiveData<Event<() -> Unit>>()
    val showAppRateSnack = MutableLiveData<Event<() -> Unit>>()
    val sharePost = MutableLiveData<Event<Post>>()
    val intentToStart = MutableLiveData<Event<IntentProvider.TYPE>>()

    val feedType: LiveData<FeedType> =
        Transformations.map(mainViewModel.isBookmarksShown) { isShown ->
            if (isShown) FeedType.BOOKMARKS else FeedType.POSTS
        }

    val postsLiveData: LiveData<PagedList<PostUIModel>> =
        Transformations.switchMap(feedType) { type ->
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
                    emptyTitle.postValue(R.string.empty_bookmark_title)
                    emptySubtitle.postValue(R.string.empty_string)
                }
                FeedType.POSTS -> {
                    emptyStateDrawable.postValue(R.drawable.ic_empty_state_bookshelf)
                    emptyTitle.postValue(R.string.empty_source_title)
                    emptySubtitle.postValue(R.string.empty_source_content)
                }
            }
            true
        } else {
            false
        }
    }

    init {
        refreshJob = refresh()
    }

    private val pagedListConfig = PagedList.Config.Builder()
        .setPageSize(ITEM_PER_PAGE)
        .setEnablePlaceholders(false)
        .build()

    private fun createPostUIModel(posts: List<Post>): List<PostUIModel> {
        /* mark first item of every page as large */
        posts.takeIf { it.isNotEmpty() }?.first()?.layoutType = UIModelType.POST_LARGE

        return posts.map { PostUIModel(it, postClickCallback) }
    }

    private val postClickCallback = object : PostClickListener {
        override fun onItemClick(post: Post) {
            openPostDetail.postValue(Event(post))
        }

        override fun onShareClick(post: Post) {
            sharePost.postValue(Event(post))
            sharedPrefs.incrementItemShareCount()
        }

        override fun onBookmarkClick(post: Post) {
            togglePostBookmark(post)
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

        viewModelScope.launch(Dispatchers.IO) {
            postRepo.updatePost(post)
        }
    }

    fun onReturnedFromPost() {
        tryShowingAppRateSnackbar()
    }

    private fun tryShowingAppRateSnackbar() {
        viewModelScope.launch(Dispatchers.IO) {
            if (appRateInteractor.isFitForAppRatePrompt(postRepo.getBookmarkedCount())) {
                viewModelScope.launch(Dispatchers.IO) {
                    showAppRateSnack.postEvent {
                        intentToStart.postEvent(IntentProvider.TYPE.RATE_APP)
                    }
                }
            }
        }
    }

    companion object {
        private const val ITEM_PER_PAGE = 20
    }
}