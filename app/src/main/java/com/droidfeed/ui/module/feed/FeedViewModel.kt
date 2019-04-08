package com.droidfeed.ui.module.feed

import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import com.droidfeed.R
import com.droidfeed.data.model.Post
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.appOpenCount
import com.droidfeed.util.appRatePrompt
import com.droidfeed.util.appRatePromptIndex
import com.droidfeed.util.event.Event
import com.droidfeed.util.extention.asLiveData
import com.droidfeed.util.shareCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST", "WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class FeedViewModel @Inject constructor(
    private val sourceRepo: SourceRepo,
    private val postRepo: PostRepo,
    private val sharedPrefs: SharedPreferences
) : BaseViewModel() {

    private val feedType = MutableLiveData<FeedType>()
    private var refreshJob = Job()

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
    val openPlayStorePage = MutableLiveData<Event<Unit>>()

    init {
        refreshJob = refresh() /* todo: causes unnecessary fetching when rebound with the fragment*/
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
                analytics.logPostClick()
            }

            override fun onShareClick(post: Post) {
                sharePost.postValue(Event(post))
                sharedPrefs.shareCount += 1
                analytics.logPostShare()
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

    fun refresh() = launch(Dispatchers.IO) {
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
                showUndoBookmarkSnack.postValue(Event({
                    togglePostBookmark(post)
                }))
            }
        } else {
            post.bookmarked = 1
            showAppRateIfCriteriaMatches()
        }

        analytics.logBookmark(post.bookmarked == 1)

        launch(Dispatchers.IO) {
            postRepo.updatePost(post)
        }
    }

    fun onReturnedFromPost() {
        showAppRateIfCriteriaMatches()
    }

    private fun showAppRateIfCriteriaMatches() {
        if (sharedPrefs.appRatePrompt) {
            launch(Dispatchers.IO) {
                val bookmarkCount = postRepo.getBookmarkedCount()

//                if (canPromptAppRate(bookmarkCount)) {
//                    analytics.logAppRatePrompt()

                    showAppRateSnack.postValue(Event({
                        openPlayStorePage.postValue(Event(Unit))
                        analytics.logAppRateFromPromtClick()
                    }))
//                }
            }
        }
    }

    private fun canPromptAppRate(bookmarkCount: Int) =
        sharedPrefs.appOpenCount > sharedPrefs.appRatePromptIndex &&
                (bookmarkCount > sharedPrefs.appRatePromptIndex ||
                        sharedPrefs.shareCount > sharedPrefs.appRatePromptIndex)

    companion object {
        private const val ITEM_PER_PAGE = 20
    }
}