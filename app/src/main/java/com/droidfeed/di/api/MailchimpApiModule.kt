package com.droidfeed.di.api

import com.droidfeed.data.api.AuthenticationInterceptor
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * Provider module for Mailchimp APIs.
 */
@Module
class MailchimpApiModule {

    @Provides
    @Named("mailchimp-http-client")
    @Singleton
    fun provideMailChimpOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        remoteConfig: FirebaseRemoteConfig
    ): OkHttpClient {
        val interceptor = AuthenticationInterceptor("apikey ${remoteConfig.getString("mc_api_key")}")

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor(interceptor)
            .build()
    }

    @Provides
    @Named("mailchimp-retrofit")
    @Singleton
    fun provideMailChimpRetrofit(@Named("mailchimp-http-client") httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://us12.api.mailchimp.com/3.0/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsletterService(@Named("mailchimp-retrofit") retrofit: Retrofit): NewsletterService {
        return retrofit.create<NewsletterService>(NewsletterService::class.java)
    }
}