package com.droidfeed.data.api

import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.model.Article
import com.droidfeed.data.parser.NewsXmlParser
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simple RSS fetcher from given url.
 */
@Suppress("UNCHECKED_CAST")
@Singleton
class RssLoader @Inject constructor(
    private val okHttpClient: OkHttpClient,
    val rssXmlParser: NewsXmlParser
) {

    fun fetch(url: String): MutableLiveData<ApiResponse<List<Article>>> {
        val fetchResponse = MutableLiveData<ApiResponse<List<Article>?>>()
        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.let {
                    val articles = response.body()?.string()?.let { it1 -> rssXmlParser.parse(it1) }
                    launch(UI) {
                        fetchResponse.value = ApiResponse(response, articles)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                launch(UI) {
                    fetchResponse.value = ApiResponse(e)
                }
            }
        })

        return fetchResponse as MutableLiveData<ApiResponse<List<Article>>>
    }
}