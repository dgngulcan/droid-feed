package com.droidfeed.data.api

import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.model.Post
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


}