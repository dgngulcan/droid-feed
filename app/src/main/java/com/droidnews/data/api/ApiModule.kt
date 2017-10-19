package com.droidnews.data.api

import com.droidnews.BuildConfig
import com.droidnews.data.api.service.NewsService
import com.droidnews.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

/**
 * Provider module for APIs.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideNewsService(): NewsService {
        val loggingInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttp = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        return Retrofit.Builder()
                .client(okHttp)
                .baseUrl("https://maps.google.com")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(NewsService::class.java)
    }

}