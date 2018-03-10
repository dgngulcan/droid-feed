package com.droidfeed.data.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import com.droidfeed.App
import com.droidfeed.data.LocalBoundResource
import com.droidfeed.data.NetworkBoundResource
import com.droidfeed.data.Resource
import com.droidfeed.data.api.ApiResponse
import com.droidfeed.data.api.RssLoader
import com.droidfeed.data.db.RssDao
import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Source
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.DebugUtils
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rss repository.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Singleton
class RssRepo @Inject constructor(
    val appContext: App,
    val dateTimeUtils: DateTimeUtils,
    val rssFeedProvider: RssLoader,
    val rssDao: RssDao
) {

    companion object {
        private const val MAX_CACHE_ITEM_COUNT = 150
        private const val NETWORK_FETCH_DIMINISHING_IN_MILLIS = 120000
    }

    private val articleResources = MediatorLiveData<Resource<List<Article>>>()

    fun getAllActiveRss(sources: LiveData<List<Source>>): LiveData<Resource<List<Article>>> =
        Transformations.switchMap(
            sources,
            {
                it.filter { it.isActive }
                    .map { source -> getRssFeed(source.url) }
                    .forEach {
                        articleResources.addSource(it,
                            {
                                articleResources.value = it
                            })
                    }
                articleResources as LiveData<Resource<List<Article>>>
            })

    private fun getRssFeed(url: String): LiveData<Resource<List<Article>>> {
        return object : NetworkBoundResource<List<Article>, ArrayList<Article>>(appContext) {

            override fun loadFromDb(): LiveData<List<Article>> = rssDao.getAllRss()

            override fun createCall(): LiveData<ApiResponse<ArrayList<Article>>> =
                rssFeedProvider.fetch(url)

            override fun saveCallResult(item: ArrayList<Article>) {
                if (rssDao.getFeedItemCount() > MAX_CACHE_ITEM_COUNT) {
                    rssDao.trimCache()
                    DebugUtils.log("Trimmed cache, removed $MAX_CACHE_ITEM_COUNT oldest article.")
                }
                rssDao.insertArticles(item)
            }

            override fun shouldFetch(data: List<Article>?): Boolean {
                if (data != null && data.isNotEmpty()) {
                    val latestCreationDate = if (data.isNotEmpty()) data.first().pubDate else ""

                    return if (latestCreationDate.isNotBlank()) {
                        latestCreationDate.let {
                            dateTimeUtils.getTimeStampFromDate(it)?.let {
                                val currentMillis = System.currentTimeMillis()
                                val timeDifference = currentMillis - it
                                timeDifference > NETWORK_FETCH_DIMINISHING_IN_MILLIS
                                        || timeDifference < 0
                            }
                        } != false
                    } else {
                        return true
                    }
                } else {
                    return true
                }
            }

            override fun onFetchFailed() {
                DebugUtils.log("onFetchFailed")
            }

            override fun processResponse(response: ApiResponse<ArrayList<Article>>):
                    ArrayList<Article>? = response.body

        }.asLiveData()
    }

    fun getBookmarkedArticles(): LiveData<Resource<List<Article>>> =
        object : LocalBoundResource<List<Article>>() {
            override fun loadFromDb(): LiveData<List<Article>> = rssDao.getBookmarkedArticles()
        }.asLiveData()

    fun updateArticle(article: Article) {
        launch {
            rssDao.updateArticle(article)
        }
    }

    fun deleteArticle(article: Article) {
        launch {
            rssDao.deleteArticle(article)
        }
    }

    fun clearSource(source: Source) {
        launch {
            rssDao.clearNonBookmarkedSource(source.name)
        }
    }

}