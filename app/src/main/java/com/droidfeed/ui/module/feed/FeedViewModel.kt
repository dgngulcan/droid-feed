package com.droidfeed.ui.module.feed

import android.arch.lifecycle.*
import android.content.Intent
import android.databinding.ObservableBoolean
import com.droidfeed.data.Resource
import com.droidfeed.data.Status
import com.droidfeed.data.model.Article
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.model.ArticleUiModel
import com.nytclient.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent


/**
 * [ViewModel] of [FeedFragment].
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Suppress("UNCHECKED_CAST")
class FeedViewModel(private val rssRepo: RssRepo, private val feedType: FeedType) : BaseViewModel() {

    val isLoading = ObservableBoolean(true)
    val loadingFailedEvent = SingleLiveEvent<Boolean>()
    val articleOpenDetail = SingleLiveEvent<Article>()
    val articleShareEvent = SingleLiveEvent<Intent>()

    private val result = MutableLiveData<List<ArticleUiModel>>()
    private val refreshToggle = MutableLiveData<Boolean>()

    private val rssResponses: LiveData<Resource<List<Article>>> =
            Transformations.switchMap(refreshToggle,
                    { loadArticles(feedType) })

    private fun loadArticles(feedType: FeedType): LiveData<Resource<List<Article>>> {
        return when (feedType) {
            FeedType.ALL -> rssRepo.getAllRss()
            FeedType.BOOKMARKS -> rssRepo.getBookmarkedArticles()
        }
    }

    val rssUiModelData: LiveData<List<ArticleUiModel>> =
            Transformations.switchMap(rssResponses, { response ->
                handleResponseStates(response)
                handleResponseData(response)
            })

    init {
        refreshToggle.value = true // initial loading trigger
    }

    private fun handleResponseData(response: Resource<List<Article>>): LiveData<List<ArticleUiModel>> {

        response.data?.let {
            var counter = 0

            result.value = (it.map { article ->
                article.layoutType = if (counter % 5 == 0 && article.image.isNotBlank()) {
                    UiModelType.ARTICLE_LARGE
                } else {
                    UiModelType.ARTICLE_SMALL
                }
                counter++
                ArticleUiModel(article, newsClickCallback)
            })
        }

        return result
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
                if (canUserClick) articleOpenDetail.setValue(article)
            }

            override fun onShareClick(article: Article) {
                if (canUserClick) articleShareEvent.setValue(article.getShareIntent())
            }

            override fun onBookmarkClick(article: Article) {
                if (canUserClick) {
                    toggleBookmark(article)
                }
            }
        }
    }

    private fun toggleBookmark(article: Article) {
        if (article.bookmarked == 1) {
            article.bookmarked = 0
        } else {
            article.bookmarked = 1
        }

        rssRepo.updateArticle(article)
    }

    /**
     * Factory class for [ViewModelProvider]. Used to pass values to constructor of the [ViewModel].
     */
    class Factory(
            private val newsRepo: RssRepo,
            private val feedType: FeedType
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>) =
                FeedViewModel(newsRepo, feedType) as T
    }

    fun onRefreshArticles() {
        refreshToggle.value = true
    }

}