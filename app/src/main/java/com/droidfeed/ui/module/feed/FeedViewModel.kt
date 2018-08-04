package com.droidfeed.ui.module.feed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.databinding.ObservableBoolean
import com.droidfeed.data.Resource
import com.droidfeed.data.Status
import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.FeedRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.model.ArticleUiModel
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
    private val rssRepo: FeedRepo
) : BaseViewModel() {

    val isLoading = ObservableBoolean()
    val isSourceEmpty = ObservableBoolean()
    val isBookmarkEmpty = ObservableBoolean()
    val loadingFailedEvent = SingleLiveEvent<Boolean>()
    val articleBookmarkEvent = SingleLiveEvent<Boolean>()
    val articleOpenDetail = SingleLiveEvent<Article>()
    val articleOnUnBookmark = SingleLiveEvent<Article>()
    val articleShareEvent = SingleLiveEvent<Intent>()

    private val result = MutableLiveData<List<ArticleUiModel>>()
    private val refreshToggle = MutableLiveData<Boolean>()
    private lateinit var feedType: FeedType

    private val rssResponses: LiveData<Resource<List<Article>>> =
        Transformations.switchMap(
            refreshToggle
        ) {
            loadFeed(feedType)
        }

    val rssUiModelData: LiveData<List<ArticleUiModel>> =
        Transformations.switchMap(rssResponses) { response ->
            handleResponseStates(response)
            handleResponseData(response)
        }

    /**
     * Sets feed type for the ViewModel. After type is set, the data is refreshed.
     */
    fun load(feedType: FeedType) {
        this.feedType = feedType
        refreshToggle.value = true // initial loading
    }

    val sources: LiveData<List<Source>> =
        Transformations.map(sourceRepo.sources) { sourceList ->
            val activeSources = sourceList.filter { it.isActive }
            isSourceEmpty.set(activeSources.isEmpty())
            isLoading.set(!activeSources.isEmpty())
            sourceList
        }

    private fun loadFeed(feedType: FeedType): LiveData<Resource<List<Article>>> =
        when (feedType) {
            FeedType.NEWS -> rssRepo.getAllFeed(sources)
            FeedType.BOOKMARKS -> rssRepo.getBookmarkedArticles()
        }

    private fun handleResponseData(response: Resource<List<Article>>): LiveData<List<ArticleUiModel>> {
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

    private fun createUiModel(article: Article, counter: Int): ArticleUiModel {
        article.layoutType = if (counter % 5 == 0 && article.image.isNotBlank()) {
            UiModelType.ARTICLE_LARGE
        } else {
            UiModelType.ARTICLE_SMALL
        }
        return ArticleUiModel(article, articleClickCallback)
    }

    private fun filterActiveArticles(articles: List<Article>): List<Article> =
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

    private fun handleResponseStates(response: Resource<List<Article>>) {
        when (response.status) {
            Status.LOADING -> {
                rssUiModelData.value?.let {
                    if (it.isEmpty()) {
                        isLoading.set(true)
                    }
                }
                loadingFailedEvent.setValue(false)
            }

            Status.SUCCESS -> {
                isLoading.set(false)
                loadingFailedEvent.setValue(false)
            }

            Status.ERROR -> {
                isLoading.set(false)
                if (loadingFailedEvent.value == false) loadingFailedEvent.setValue(true)
            }
        }
    }

    private val articleClickCallback by lazy {
        object : ArticleClickListener {
            override fun onItemClick(article: Article) {
                if (canClick) {
                    articleOpenDetail.setValue(article)
                }
            }

            override fun onShareClick(article: Article) {
                if (canClick) {
                    articleShareEvent.setValue(article.getShareIntent())
                }
            }

            override fun onBookmarkClick(article: Article) {
                if (canClick) {
                    toggleBookmark(article)
                    articleBookmarkEvent.setValue(true)
                }
            }
        }
    }

    fun toggleBookmark(article: Article) {
        if (article.bookmarked == 1) {
            article.bookmarked = 0
            articleOnUnBookmark.setValue(article)
        } else {
            article.bookmarked = 1
        }

        rssRepo.updateArticle(article)
    }

    fun onRefreshArticles() {
        refreshToggle.value = true
    }
}