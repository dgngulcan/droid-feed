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
    private val feedRepo: PostRepo
) : BaseViewModel() {

    val feedType = MutableLiveData<FeedType>()
    val sources = sourceRepo.sources

    private val repoResult = map(feedType) { type ->
        when (type) {
            FeedType.POSTS -> {
                feedRepo.getAllPosts(sources) { createUiModels(it) }
            }
            FeedType.BOOKMARKS -> {
                feedRepo.getBookmarkedPosts { createUiModels(it) }
            }
        }
    }

    val networkState = switchMap(repoResult) { it.networkState }!!
    val posts: LiveData<PagedList<PostUIModel>> = switchMap(repoResult) { it.pagedList }

    val postBookmarkEvent = SingleLiveEvent<Boolean>()
    val postOpenDetail = SingleLiveEvent<Post>()
    val postUnBookmarkEvent = SingleLiveEvent<Post>()
    val postShareEvent = SingleLiveEvent<Intent>()

    private fun createUiModels(posts: List<Post>): List<PostUIModel> {
        if (posts.isNotEmpty()) {
            posts[0].layoutType = UiModelType.POST_LARGE
        }
        return posts.map { PostUIModel(it, postClickCallback) }
    }

    private val postClickCallback by lazy {
        object : PostClickListener {
            override fun onItemClick(post: Post) {
                if (canClick) {
                    postOpenDetail.setValue(post)
                }
            }

            override fun onShareClick(post: Post) {
                if (canClick) {
                    postShareEvent.setValue(post.getShareIntent())
                }
            }

            override fun onBookmarkClick(post: Post) {
                if (canClick) {
                    toggleBookmark(post)
                }
            }
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

        feedRepo.updatePost(article)
    }
}