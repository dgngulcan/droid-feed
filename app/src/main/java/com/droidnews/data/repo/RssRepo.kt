package com.droidnews.data.repo

import android.arch.lifecycle.LiveData
import com.droidnews.App
import com.droidnews.data.NetworkBoundResource
import com.droidnews.data.Resource
import com.droidnews.data.api.ApiResponse
import com.droidnews.data.api.service.NewsService
import com.droidnews.data.db.RssDao
import com.droidnews.data.model.RssItem
import com.droidnews.data.model.RssResponse
import com.droidnews.util.DateTimeUtils
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
                                  val rssDao: RssDao,
                                  val newsService: NewsService) {

    fun getAndroidPubFeed(): LiveData<Resource<RssResponse>> {
        return object : NetworkBoundResource<List<RssItem>, RssResponse>(appContext) {

            override fun loadFromDb(): LiveData<List<RssItem>> {
                return rssDao.getAllRss()
            }

            override fun createCall(): LiveData<ApiResponse<RssResponse>> {
                return newsService.getAndroidPubFeed()
            }

            override fun saveCallResult(item: RssResponse) {
                rssDao.insertRss(*item.rss.channel.rss)
            }

            override fun shouldFetch(data: List<RssItem>?): Boolean {
                if (data != null) {
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

            }

            override fun processResponse(response: ApiResponse<RssResponse>): RssResponse? {
                return response.body
            }

        }.asLiveData()
    }

}