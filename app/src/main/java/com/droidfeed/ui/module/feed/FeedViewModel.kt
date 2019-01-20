package com.droidfeed.ui.module.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Post
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import com.droidfeed.util.extention.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST", "WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class FeedViewModel @Inject constructor(
    private val sourceRepo: SourceRepo,
    private val postRepo: PostRepo
) : BaseViewModel() {

    val feedType = MutableLiveData<FeedType>()

    val activeSourceCountLiveData = sourceRepo.getActiveSourceCount()

    val postsLiveData: LiveData<PagedList<PostUIModel>> = switchMap(feedType) { type ->
        when (type) {
            FeedType.POSTS -> {
                postRepo.getAllPosts()
                    .asLiveData(pagedListConfig) { posts ->
                        createPostUIModel(posts)
                    }
            }
            FeedType.BOOKMARKS -> {
                postRepo.getBookmarkedPosts()
                    .asLiveData(pagedListConfig) { posts ->
                        createPostUIModel(posts)
                    }
            }
        }
    }

    init {
        refresh()
    }

    val networkState = MutableLiveData<DataStatus<Nothing>>()
    val postBookmarkEvent = MutableLiveData<Event<Nothing>>()
    val postOpenDetail = MutableLiveData<Event<Post>>()
    val postUnBookmarkEvent = MutableLiveData<Event<Post>>()
    val postShareEvent = MutableLiveData<Event<Post>>()

    private val pagedListConfig = PagedList.Config.Builder()
        .setPageSize(30)
        .setEnablePlaceholders(false)
        .build()

    private fun createPostUIModel(posts: List<Post>): List<PostUIModel> {
        if (posts.isNotEmpty()) {
            posts[0].layoutType = UIModelType.POST_LARGE
        }
        return posts.map { PostUIModel(it, postClickCallback) }
    }

    private val postClickCallback =
        object : PostClickListener {
            override fun onItemClick(post: Post) {
                postOpenDetail.postValue(Event(post))
            }

            override fun onShareClick(post: Post) {
                postShareEvent.postValue(Event(post))
            }

            override fun onBookmarkClick(post: Post) {
                toggleBookmark(post)
            }
        }

    fun setFeedType(feedType: FeedType) {
        if (this.feedType.value != feedType) {
            this.feedType.value = feedType
        }
    }

    fun refresh() = launch(Dispatchers.IO) {
        networkState.postValue(DataStatus.Loading())
        postRepo.refresh(sourceRepo.getActiveSources())
        networkState.postValue(DataStatus.Successful())
    }

    fun toggleBookmark(post: Post) {
        if (post.isBookmarked()) {
            post.bookmarked = 0
            postUnBookmarkEvent.postValue(Event(post))
        } else {
            post.bookmarked = 1
            postBookmarkEvent.postValue(Event())
        }

        launch(Dispatchers.IO) {
            postRepo.updatePost(post)
        }
    }
}