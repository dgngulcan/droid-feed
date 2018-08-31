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
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent
import javax.inject.Inject

@Suppress("UNCHECKED_CAST", "WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class FeedViewModel @Inject constructor(
    sourceRepo: SourceRepo,
    private val feedRepo: PostRepo
) : BaseViewModel() {

    private val feedType = MutableLiveData<FeedType>()
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

    val articleBookmarkEvent = SingleLiveEvent<Boolean>()
    val articleOpenDetail = SingleLiveEvent<Post>()
    val articleUnBookmarkEvent = SingleLiveEvent<Post>()
    val articleShareEvent = SingleLiveEvent<Intent>()

    private fun createUiModels(posts: List<Post>) =
        posts.map { PostUIModel(it, articleClickCallback) }

    private val articleClickCallback by lazy {
        object : PostClickListener {
            override fun onItemClick(article: Post) {
                if (canClick) {
                    articleOpenDetail.setValue(article)
                }
            }

            override fun onShareClick(article: Post) {
                if (canClick) {
                    articleShareEvent.setValue(article.getShareIntent())
                }
            }

            override fun onBookmarkClick(article: Post) {
                if (canClick) {
                    toggleBookmark(article)
                    articleBookmarkEvent.setValue(true)
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
            articleUnBookmarkEvent.setValue(article)
        } else {
            article.bookmarked = 1
        }

        feedRepo.updatePost(article)
    }
}