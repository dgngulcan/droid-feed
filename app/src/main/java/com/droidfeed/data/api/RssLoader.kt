package com.droidfeed.data.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.model.Article
import com.droidfeed.data.RssXmlParser
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.*
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple RSS fetcher from given url.
 *
 * Created by Dogan Gulcan on 10/31/17.
 */
@Suppress("UNCHECKED_CAST")
@Singleton
class RssLoader @Inject constructor(val okHttpClient: OkHttpClient) {

    fun fetch(url: String): LiveData<ApiResponse<ArrayList<Article>>> {

        val fetchResponse = MutableLiveData<ApiResponse<ArrayList<Article>?>>()
        val request = Request.Builder()
                .url(url)
                .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.let {
                    val articles = response.body()?.string()?.let { it1 -> RssXmlParser().parse(it1) }
                    async(UI) {
                        fetchResponse.value = ApiResponse(response, articles)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                async(UI) {
                    fetchResponse.value = ApiResponse(e)
                }
            }

        })

        return fetchResponse as LiveData<ApiResponse<ArrayList<Article>>>
    }

}

