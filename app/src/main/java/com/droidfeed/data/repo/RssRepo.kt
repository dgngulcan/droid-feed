package com.droidfeed.data.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.droidfeed.App
import com.droidfeed.data.LocalBoundResource
import com.droidfeed.data.NetworkBoundResource
import com.droidfeed.data.Resource
import com.droidfeed.data.api.ApiResponse
import com.droidfeed.data.api.RssLoader
import com.droidfeed.data.db.RssDao
import com.droidfeed.data.model.Article
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.DebugUtils
import org.jetbrains.anko.coroutines.experimental.bg
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
        private val MAX_CACHE_ITEM_COUNT = 150
        private val NETWORK_FETCH_DIMINISHING_IN_MILLIS = 100
    }

    private fun getRssSourceList(): List<String> {
        return listOf(
                "https://android.jlelse.eu/feed",
                "https://proandroiddev.com/feed",
                "https://medium.com/feed/google-developers",
                "https://rss.simplecast.com/podcasts/3213/rss",
                "http://androidbackstage.blogspot.com/feeds/posts/default?alt=rss", // droid snacks
//                "https://www.youtube.com/feeds/videos.xml?channel_id=UCVHFbqXqoYvEWM1Ddxl0QDg", // android dev youtube channel
                "http://fragmentedpodcast.com/feed"
        )
    }

    fun getAllRss(): MediatorLiveData<Resource<List<Article>>> {
        val resources = MediatorLiveData<Resource<List<Article>>>()

        getRssSourceList().map { getRssFeed(it) }
                .forEach {
                    resources.addSource(it,
                            { response ->
                                resources.value = response
                            })
                }

        return resources
    }

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
                            dateTimeUtils.getTimeStampFromDate(it)?.
                                    let {
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
                // todo let user know!
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
        bg {
            rssDao.updateArticle(article)
        }
    }

    fun deleteArticle(article: Article) {
        bg {
            rssDao.deleteArticle(article)
        }
    }

}