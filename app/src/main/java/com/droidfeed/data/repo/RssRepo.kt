package com.droidfeed.data.repo

import android.arch.lifecycle.LiveData
import com.droidfeed.App
import com.droidfeed.data.NetworkBoundResource
import com.droidfeed.data.Resource
import com.droidfeed.data.api.ApiResponse
import com.droidfeed.data.api.RssLoader
import com.droidfeed.data.db.RssDao
import com.droidfeed.data.model.Article
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.DebugUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * News repository.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Singleton
class RssRepo @Inject constructor(val appContext: App,
                                  val dateTimeUtils: DateTimeUtils,
                                  val rssFeedProvider: RssLoader,
                                  val rssDao: RssDao) {

    fun getRssFeed(url: String): LiveData<Resource<List<Article>>> {
        return object : NetworkBoundResource<List<Article>, ArrayList<Article>>(appContext) {

            override fun loadFromDb(): LiveData<List<Article>> {
                return rssDao.getAllRss()
            }

            override fun createCall(): LiveData<ApiResponse<ArrayList<Article>>> {
                return rssFeedProvider.fetch(url)
            }

            override fun saveCallResult(item: ArrayList<Article>) {
                rssDao.insertRss(item)
            }

            override fun shouldFetch(data: List<Article>?): Boolean {
                if (data != null && data.isNotEmpty()) {
                    val latestNewsCreationDate = if (data.isNotEmpty()) {
                        data.first().pubDate
                    } else {
                        ""
                    }
                    return if (latestNewsCreationDate.isNotBlank()) {
                        latestNewsCreationDate.let {
                            dateTimeUtils.getTimeStampFromDate(it)?.
                                    let {
                                        val currentMillis = System.currentTimeMillis()
                                        val timeDifference = currentMillis - it
                                        TimeUnit.MILLISECONDS.toMinutes(timeDifference) > 2
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

            override fun processResponse(response: ApiResponse<ArrayList<Article>>): ArrayList<Article>? {
                return response.body
            }

        }.asLiveData()
    }

}