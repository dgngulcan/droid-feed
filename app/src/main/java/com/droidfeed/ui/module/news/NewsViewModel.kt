package com.droidfeed.ui.module.news

import android.arch.lifecycle.*
import android.databinding.ObservableBoolean
import android.view.View
import com.droidfeed.data.Resource
import com.droidfeed.data.Status
import com.droidfeed.data.model.Article
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.ui.adapter.model.RssListUiModel
import com.nytclient.ui.common.BaseViewModel
import com.nytclient.ui.common.SingleLiveEvent

/**
 * [ViewModel] of [NewsFragment].
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Suppress("UNCHECKED_CAST")
class NewsViewModel(val rssRepo: RssRepo) : BaseViewModel() {

    var isLoadingNews = ObservableBoolean(false)
    var loadingFailedEvent = SingleLiveEvent<Boolean>()
    var openRssClickEvent = SingleLiveEvent<Pair<Article, View>>()

    private var rssUrls: List<String> = listOf(
            "https://android.jlelse.eu/feed")

    private var rssResponses = MediatorLiveData<Resource<List<Article>>>()

    var rssUiModelData: LiveData<List<RssListUiModel>> =
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

                val result = MutableLiveData<List<RssListUiModel>>()
                response.data.let {
                    result.value = (response as Resource<List<Article>>)
                            .data?.map { RssListUiModel(it, newsClickCallback) }
                }

                result
            })

    private val newsClickCallback by lazy {
        object : NewsItemClickListener {
            override fun onItemClick(view: View, rssItem: Article) {
                openRssClickEvent.setValue(Pair(rssItem, view))
            }

            override fun onShareClick(rssItem: Article) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onBookmarkClick(rssItem: Article) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
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
            return NewsViewModel(newsRepo) as T
        }
    }
}