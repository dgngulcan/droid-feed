package com.droidfeed.ui.module.feed

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import com.droidfeed.data.model.Post
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent
import javax.inject.Inject

@Suppress("UNCHECKED_CAST", "WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class FeedViewModel @Inject constructor(
    sourceRepo: SourceRepo,
    private val postRepo: PostRepo
) : BaseViewModel() {

    val feedType = MutableLiveData<FeedType>()
    val sources = sourceRepo.sources

    private val repoResult = map(feedType) { type ->
        when (type) {
            FeedType.POSTS -> {
                postRepo.getAllPosts(sources) { createPostUIModel(it) }
            }
            FeedType.BOOKMARKS -> {
                postRepo.getBookmarkedPosts { createPostUIModel(it) }
            }
        }
    }

    val networkState = switchMap(repoResult) { it.networkState }!!
    val posts: LiveData<PagedList<PostUIModel>> = switchMap(repoResult) { it.pagedList }

    val postBookmarkEvent = SingleLiveEvent<Boolean>()
    val postOpenDetail = SingleLiveEvent<Post>()
    val postUnBookmarkEvent = SingleLiveEvent<Post>()
    val postShareEvent = SingleLiveEvent<Intent>()

    private fun createPostUIModel(posts: List<Post>): List<PostUIModel> {
        if (posts.isNotEmpty()) {
            posts[0].layoutType = UiModelType.POST_LARGE
        }
        return posts.map { PostUIModel(it, postClickCallback) }
    }

    private val postClickCallback =
        object : PostClickListener {
            override fun onItemClick(post: Post) {
                postOpenDetail.setValue(post)
            }

            override fun onShareClick(post: Post) {
                postShareEvent.setValue(post.getShareIntent())
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

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun toggleBookmark(article: Post) {
        if (article.bookmarked == 1) {
            article.bookmarked = 0
            postUnBookmarkEvent.setValue(article)
        } else {
            article.bookmarked = 1
            postBookmarkEvent.setValue(true)
        }

        postRepo.updatePost(article)
    }
}