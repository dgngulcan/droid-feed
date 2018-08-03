package com.droidfeed.di

import com.droidfeed.App
import com.droidfeed.BuildConfig
import com.droidfeed.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provider module for Firebase APIs.
 */
@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(app: App): FirebaseAnalytics = FirebaseAnalytics.getInstance(app)

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        var cacheDuration = 3600L
        if (remoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheDuration = 0
        }

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()

        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(R.xml.remote_config_defaults)

        remoteConfig.fetch(cacheDuration)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    remoteConfig.activateFetched()
                }
            }
        return remoteConfig
    }
}