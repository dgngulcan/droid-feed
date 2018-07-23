package com.droidfeed.data.api

import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.parser.NewsXmlParser
import com.droidfeed.data.model.Article
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
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

