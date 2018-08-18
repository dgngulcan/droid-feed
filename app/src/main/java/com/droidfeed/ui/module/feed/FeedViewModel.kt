package com.droidfeed.ui.module.feed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.databinding.ObservableBoolean
import com.droidfeed.data.DataResource
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * [ViewModel] for feed screens.
 */
@Suppress("UNCHECKED_CAST")
class FeedViewModel @Inject constructor(
    sourceRepo: SourceRepo,
    private val rssRepo: PostRepo
) : BaseViewModel() {

    val isLoading = ObservableBoolean()
    val isSourceEmpty = ObservableBoolean()
    val isBookmarkEmpty = ObservableBoolean()
    val loadingFailedEvent = SingleLiveEvent<Boolean>()
    val articleBookmarkEvent = SingleLiveEvent<Boolean>()
    val articleOpenDetail = SingleLiveEvent<Post>()
    val articleOnUnBookmark = SingleLiveEvent<Post>()
    val articleShareEvent = SingleLiveEvent<Intent>()

    private val result = MutableLiveData<List<PostUIModel>>()
    private val refreshToggle = MutableLiveData<Boolean>()
    private lateinit var feedType: FeedType

    private val rssResponses: LiveData<DataResource<List<Post>>> =
        Transformations.switchMap(
            refreshToggle
        ) {
            loadFeed(feedType)
        }

    val rssUiModelData: LiveData<List<PostUIModel>> =
        Transformations.switchMap(rssResponses) { response ->
            handleResponseStates(response)
            handleResponseData(response)
        }

    val sources: LiveData<List<Source>> =
        Transformations.map(sourceRepo.sources) { sourceList ->
            val activeSources = sourceList.filter { it.isActive }
            isSourceEmpty.set(activeSources.isEmpty())
            isLoading.set(!activeSources.isEmpty())
            sourceList
        }

    /**
     * Sets feed type for the ViewModel. After type is set, the data is refreshed.
     */
    fun load(feedType: FeedType) {
        this.feedType = feedType
        refreshToggle.value = true // initial loading
    }

    private fun loadFeed(feedType: FeedType): LiveData<DataResource<List<Post>>> = MutableLiveData()
//        when (feedType) {
//            FeedType.NEWS -> rssRepo.refresh(sources)
//            FeedType.BOOKMARKS -> rssRepo.getBookmarkedArticles()
//}

    private fun handleResponseData(response: DataResource<List<Post>>): LiveData<List<PostUIModel>> {
        response.data?.let { articleList ->
            launch(CommonPool) {
                val articles = if (feedType == FeedType.NEWS) {
                    filterActiveArticles(articleList)
                } else {
                    isBookmarkEmpty.set(articleList.isEmpty())
                    articleList
                }

                var index = -1
                val uiModels = articles.map { article ->
                    index++
                    createUiModel(article, index)
                }

                result.postValue(uiModels)
            }
        }

        return result
    }

    private fun createUiModel(article: Post, counter: Int): PostUIModel {
        article.layoutType = if (counter % 5 == 0 && article.image.isNotBlank()) {
            UiModelType.POST_LARGE
        } else {
            UiModelType.POST_SMALL
        }
        return PostUIModel(article, articleClickCallback)
    }

    private fun filterActiveArticles(articles: List<Post>): List<Post> =
        articles.filter { article ->
            var isActive = false
            sources.value?.forEach {
                if (it.isActive && article.channel.title.contains(it.name)) {
                    isActive = true
                    return@forEach
                }
            }
            isActive
        }

    private fun handleResponseStates(response: DataResource<List<Post>>) {
//        when (response.dataState) {
//            Status.LOADING -> {
//                rssUiModelData.value?.let {
//                    if (it.isEmpty()) {
//                        isLoading.set(true)
//                    }
//                }
//                loadingFailedEvent.setValue(false)
//            }
//
//            Status.SUCCESS -> {
//                isLoading.set(false)
//                loadingFailedEvent.setValue(false)
//            }
//
//            Status.ERROR -> {
//                isLoading.set(false)
//                if (loadingFailedEvent.value == false) loadingFailedEvent.setValue(true)
//            }
//        }
    }

    private val articleClickCallback by lazy {
        object : ArticleClickListener {
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

    fun toggleBookmark(article: Post) {
        if (article.bookmarked == 1) {
            article.bookmarked = 0
            articleOnUnBookmark.setValue(article)
        } else {
            article.bookmarked = 1
        }

//        rssRepo.updateArticle(article)
    }

    fun onRefreshArticles() {
        refreshToggle.value = true
    }
}