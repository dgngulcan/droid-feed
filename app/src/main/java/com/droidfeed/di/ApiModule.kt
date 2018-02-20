package com.droidfeed.di

import com.droidfeed.App
import com.droidfeed.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
    fun provideOkHttpClient(): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
//            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(app: App): FirebaseAnalytics = FirebaseAnalytics.getInstance(app)


}