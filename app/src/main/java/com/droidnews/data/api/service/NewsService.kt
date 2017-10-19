package com.droidnews.data.api.service

import android.arch.lifecycle.LiveData
import com.droidnews.data.api.ApiResponse
import com.droidnews.data.model.RssResponse
import retrofit2.http.GET

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
interface NewsService {

    @GET("https://android-developers.googleblog.com/atom.xml")
    fun getAndroidDevelopersGoogleBlogFeed(): LiveData<ApiResponse<RssResponse>>

    @GET("https://android.jlelse.eu/feed")
    fun getAndroidPubFeed(): LiveData<ApiResponse<RssResponse>>


}