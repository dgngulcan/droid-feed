package com.droidfeed.ui.module.feed

import android.arch.lifecycle.*
import android.content.Intent
import android.databinding.ObservableBoolean
import com.droidfeed.data.Resource
import com.droidfeed.data.Status
import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.model.ArticleUiModel
import com.droidfeed.ui.common.SingleLiveEvent
import com.droidfeed.util.AnalyticsUtil
import com.droidfeed.ui.common.BaseViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch


/**
 * [ViewModel] of [FeedFragment].
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Suppress("UNCHECKED_CAST")
class FeedViewModel(
    private val rssRepo: RssRepo,
    sourceRepo: SourceRepo,
    private val feedType: FeedType,
    private val analytics: AnalyticsUtil
) : BaseViewModel() {

    val isLoading = ObservableBoolean(true)
    val loadingFailedEvent = SingleLiveEvent<Boolean>()
    val noSourceSelected = SingleLiveEvent<Boolean>()
    val noBookmarkedArticle = SingleLiveEvent<Boolean>()
    val articleOpenDetail = SingleLiveEvent<Article>()
    val articleOnUnBookmark = SingleLiveEvent<Article>()
    val articleShareEvent = SingleLiveEvent<Intent>()

    private val result = MutableLiveData<List<ArticleUiModel>>()
    private val refreshToggle = MutableLiveData<Boolean>()

    private val rssResponses: LiveData<Resource<List<Article>>> =
        Transformations.switchMap(refreshToggle,
            {
                loadArticles(feedType)
            })

    val rssUiModelData: LiveData<List<ArticleUiModel>> =
        Transformations.switchMap(rssResponses, { response ->
            handleResponseStates(response)
            handleResponseData(response)
        })

    init {
        refreshToggle.value = true // initial loading trigger
    }

    val sources: LiveData<List<Source>> = Transformations.map(sourceRepo.sources, { sourceList ->
        val activeSources = sourceList.filter { it.isActive }
        noSourceSelected.setValue(activeSources.isEmpty())
        isLoading.set(!activeSources.isEmpty())
        sourceList
    })

    private fun loadArticles(feedType: FeedType): LiveData<Resource<List<Article>>> {
        return when (feedType) {
            FeedType.ALL -> rssRepo.getAllActiveRss(sources)
            FeedType.BOOKMARKS -> rssRepo.getBookmarkedArticles()
        }
    }

    private fun handleResponseData(response: Resource<List<Article>>): LiveData<List<ArticleUiModel>> {
        response.data?.let { articleList ->
            launch(CommonPool) {
                val articles = if (feedType == FeedType.ALL) {
                    filterActiveArticles(articleList)
                } else {
                    noBookmarkedArticle.postValue(articleList.isEmpty())
                    articleList
                }

                var index = -1
                val uiModels = articles.map { article ->
                    index++
                    generateUiModel(article, index)
                }

                result.postValue(uiModels)
            }
        }

        return result
    }


    private fun generateUiModel(article: Article, counter: Int): ArticleUiModel {
        article.layoutType = if (counter % 5 == 0 && article.image.isNotBlank()) {
            UiModelType.ARTICLE_LARGE
        } else {
            UiModelType.ARTICLE_SMALL
        }
        return ArticleUiModel(article, newsClickCallback)
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
                rssUiModelData.value?.let { if (it.isEmpty()) isLoading.set(true) }
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

    private val newsClickCallback by lazy {
        object : ArticleClickListener {
            override fun onItemClick(article: Article) {
                if (userCanClick) {
                    articleOpenDetail.setValue(article)
                    analytics.logArticleClick()
                }
            }

            override fun onShareClick(article: Article) {
                if (userCanClick) {
                    articleShareEvent.setValue(article.getShareIntent())
                    analytics.logShare()
                }
            }

            override fun onBookmarkClick(article: Article) {
                if (userCanClick) {
                    toggleBookmark(article)
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

        analytics.logBookmark(article.bookmarked == 1)
        rssRepo.updateArticle(article)
    }

    /**
     * Factory class for [ViewModelProvider]. Used to pass values to constructor of the [ViewModel].
     */
    class Factory(
        private val newsRepo: RssRepo,
        private val sourceRepo: SourceRepo,
        private val feedType: FeedType,
        private val analytics: AnalyticsUtil
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            FeedViewModel(newsRepo, sourceRepo, feedType, analytics) as T
    }

    fun onRefreshArticles() {
        refreshToggle.value = true
    }


}