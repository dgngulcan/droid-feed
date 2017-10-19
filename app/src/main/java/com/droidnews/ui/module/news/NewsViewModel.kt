package com.droidnews.ui.module.news

import android.arch.lifecycle.*
import android.databinding.ObservableBoolean
import android.view.View
import com.droidnews.data.Resource
import com.droidnews.data.Status
import com.droidnews.data.model.RssItem
import com.droidnews.data.model.RssResponse
import com.droidnews.data.repo.RssRepo
import com.droidnews.ui.common.ListItemClickListener
import com.droidnews.ui.model.RssListUiModel
import com.nytclient.ui.common.BaseViewModel
import com.nytclient.ui.common.SingleLiveEvent

/**
 * [ViewModel] of [NewsFragment].
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Suppress("UNCHECKED_CAST")
class NewsViewModel(rssRepo: RssRepo) : BaseViewModel() {

    var isLoadingNews = ObservableBoolean(false)
    var loadingFailedevent = SingleLiveEvent<Boolean>()
    var openRssClickEvent = SingleLiveEvent<Pair<RssItem, View>>()

    private var rssApiResponse: LiveData<Resource<RssResponse>>
            = rssRepo.getAndroidPubFeed()

    var rssUiModelData: LiveData<List<RssListUiModel>> =
            Transformations.switchMap(rssApiResponse, { response ->

                loadingFailedevent.setValue(false)

                when (response.status) {
                    Status.LOADING -> isLoadingNews.set(true)
                    Status.SUCCESS -> isLoadingNews.set(false)
                    Status.ERROR -> {
                        isLoadingNews.set(false)
                        loadingFailedevent.setValue(true)
                    }
                }

                val result = MutableLiveData<List<RssListUiModel>>()
                result.value = (response as Resource<List<RssItem>>)
                        .data.map { RssListUiModel(it, rssClickCallback) }
                result
            })

    private val rssClickCallback = object : ListItemClickListener<RssItem> {
        override fun onItemClick(obj: RssItem, view: View) {
            openRssClickEvent.setValue(Pair(obj, view))
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