package com.droidfeed.di

import com.droidfeed.App
import com.droidfeed.BuildConfig
import com.droidfeed.data.api.mailchimp.ErrorAdapter
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.google.firebase.analytics.FirebaseAnalytics
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton


/**
 * Provider module for APIs.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(app: App): FirebaseAnalytics = FirebaseAnalytics.getInstance(app)

    @Provides
    @Named("mailchimp-retrofit")
    @Singleton
    fun provideMailChimpRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(ErrorAdapter())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://us12.api.mailchimp.com/3.0/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsletterService(@Named("mailchimp-retrofit") retrofit: Retrofit): NewsletterService =
        retrofit.create<NewsletterService>(NewsletterService::class.java)

}