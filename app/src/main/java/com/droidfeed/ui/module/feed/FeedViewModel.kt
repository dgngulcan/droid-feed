package com.droidfeed.ui.module.feed

import android.arch.lifecycle.*
import android.content.Intent
import android.databinding.ObservableBoolean
import com.droidfeed.data.Resource
import com.droidfeed.data.Status
import com.droidfeed.data.model.Article
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.ui.adapter.model.ArticleUiModel
import com.nytclient.ui.common.BaseViewModel
import com.nytclient.ui.common.SingleLiveEvent


/**
 * [ViewModel] of [FeedFragment].
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Suppress("UNCHECKED_CAST")
class FeedViewModel(private val rssRepo: RssRepo) : BaseViewModel() {

    var isLoadingNews = ObservableBoolean(false)
    var loadingFailedEvent = SingleLiveEvent<Boolean>()
    var articleClickEvent = SingleLiveEvent<Article>()
    var articleShareEvent = SingleLiveEvent<Intent>()

    private var rssUrls: List<String> = listOf(
            "https://android.jlelse.eu/feed")

    private var rssResponses = MediatorLiveData<Resource<List<Article>>>()

    var rssUiModelData: LiveData<List<ArticleUiModel>> =
            Transformations.switchMap(rssResponses, { response ->

                loadingFailedEvent.setValue(false)

                when (response.status) {
                    Status.LOADING -> isLoadingNews.set(true)
                    Status.SUCCESS -> isLoadingNews.set(false)
                    Status.ERROR -> {
                        isLoadingNews.set(false)
                        loadingFailedEvent.setValue(true)
                    }
                }

                val result = MutableLiveData<List<ArticleUiModel>>()
                response.data.let {
                    result.value = (response as Resource<List<Article>>)
                            .data?.map { ArticleUiModel(it, newsClickCallback) }
                }

                result
            })

    private val newsClickCallback by lazy {
        object : ArticleClickListener {
            override fun onItemClick(article: Article) {
                if (canUserClick) articleClickEvent.setValue(article)
            }

            override fun onShareClick(article: Article) {
                if (canUserClick) {
                    articleShareEvent.setValue(createArticleShareIntent(article))
                }
            }

            override fun onBookmarkClick(article: Article) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    init {
        loadRssUrls()
    }

    private fun loadRssUrls() {

        rssUrls.map { rssRepo.getRssFeed(it) }
                .forEach {
                    rssResponses.addSource(it,
                            { response ->
                                loadingFailedEvent.setValue(false)

                                when (response?.status) {
                                    Status.LOADING -> isLoadingNews.set(true)
                                    Status.SUCCESS -> {
                                        isLoadingNews.set(false)
                                        rssResponses.removeSource(it)
                                    }
                                    Status.ERROR -> {
                                        isLoadingNews.set(false)
                                        rssResponses.removeSource(it)
                                        loadingFailedEvent.setValue(true)
                                    }
                                }

                                rssResponses.value = response
                            })
                }
    }

    /**
     * Factory class for [ViewModelProvider]. Used to pass values to constructor of the [ViewModel].
     */
    class Factory(private val newsRepo: RssRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedViewModel(newsRepo) as T
        }
    }
}