package com.droidfeed.ui.module.feed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.map
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent
import javax.inject.Inject

/**
 * [ViewModel] for feed screens.
 */
@Suppress("UNCHECKED_CAST")
class FeedViewModel2 @Inject constructor(
    sourceRepo: SourceRepo,
    private val feedRepo: PostRepo
) : BaseViewModel() {

    private val sources: LiveData<List<Source>> = sourceRepo.sources
    private val feedType = MutableLiveData<FeedType>()
    private val repoResult = map(feedType) { feedRepo.getAllPosts(sources) { posts -> createUiModels(posts) } }

    val networkState = switchMap(repoResult) { it.networkState }!!
    val posts: LiveData<PagedList<PostUIModel>> = switchMap(repoResult) { it.pagedList }
    val articleBookmarkEvent = SingleLiveEvent<Boolean>()

    private fun createUiModels(posts: List<Post>) = posts.map { PostUIModel(it, articleClickCallback) }

    private val articleClickCallback by lazy {
        object : ArticleClickListener {
            override fun onItemClick(article: Post) {
                if (canClick) {
//                    articleOpenDetail.setValue(article)
                }
            }

            override fun onShareClick(article: Post) {
                if (canClick) {
//                    articleShareEvent.setValue(article.getShareIntent())
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

    @MainThread
    fun setFeedType(feedType: FeedType) {
        this.feedType.value = feedType
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun toggleBookmark(article: Post) {
        if (article.bookmarked == 1) {
            article.bookmarked = 0
//            articleOnUnBookmark.setValue(article)
        } else {
            article.bookmarked = 1
        }

        feedRepo.updatePost(article)
    }
}