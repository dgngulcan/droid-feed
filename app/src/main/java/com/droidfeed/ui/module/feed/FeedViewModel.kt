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
import com.nytclient.ui.common.SingleLiveEvent


/**
 * [ViewModel] of [FeedFragment].
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Suppress("UNCHECKED_CAST")
class FeedViewModel(rssRepo: RssRepo) : BaseViewModel() {

    var isLoadingNews = ObservableBoolean(false)
    var loadingFailedEvent = SingleLiveEvent<Boolean>()
    var articleClickEvent = SingleLiveEvent<Article>()
    var articleShareEvent = SingleLiveEvent<Intent>()

    private var rssResponses = rssRepo.getAllRss()

    var rssUiModelData: LiveData<List<ArticleUiModel>> =
            Transformations.switchMap(rssResponses, { response ->
                handleResponseStates(response)
                handleResponseData(response)
            })

    private fun handleResponseData(response: Resource<List<Article>>): MutableLiveData<List<ArticleUiModel>> {
        val result = MutableLiveData<List<ArticleUiModel>>()

        response.data?.let {
            val sortedList = it.sortedWith(compareByDescending(Article::pubDateTimestamp))
            var counter = 0

            result.value = sortedList.map { article ->
                article.layoutType = if (counter % 5 == 0 && article.image.isNotBlank()) {
                    UiModelType.ArticleLarge
                } else {
                    UiModelType.ArticleSmall
                }
                counter++
                ArticleUiModel(article, newsClickCallback)
            }
        }

        return result
    }

    private fun handleResponseStates(response: Resource<List<Article>>) {
        loadingFailedEvent.setValue(false)

        when (response.status) {
            Status.LOADING -> isLoadingNews.set(true)
            Status.SUCCESS -> isLoadingNews.set(false)
            Status.ERROR -> {
                isLoadingNews.set(false)
                loadingFailedEvent.setValue(true)
            }
        }
    }

    private val newsClickCallback by lazy {
        object : ArticleClickListener {
            override fun onItemClick(article: Article) {
                if (canUserClick) articleClickEvent.setValue(article)
            }

            override fun onShareClick(article: Article) {
                if (canUserClick) articleShareEvent.setValue(createArticleShareIntent(article))

            }

            override fun onBookmarkClick(article: Article) {
                if (canUserClick) {
                    article.bookmarked = if (article.bookmarked == 1) 0 else 1
                    rssRepo.addBookmarkedArticle(article)
                }
            }
        }
    }

    private fun createArticleShareIntent(article: Article): Intent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.link}")
        sendIntent.type = "text/plain"
        return sendIntent
    }

    /**
     * Factory class for [ViewModelProvider]. Used to pass values to constructor of the [ViewModel].
     */
    class Factory(private val newsRepo: RssRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = FeedViewModel(newsRepo) as T
    }
}