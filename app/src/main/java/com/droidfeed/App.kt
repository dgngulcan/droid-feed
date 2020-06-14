package com.droidfeed

import android.app.Application
import com.droidfeed.data.repo.SharedPrefsRepo
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject lateinit var sharedPrefs: SharedPrefsRepo

    @Suppress("unused")
    @Inject lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()

        sharedPrefs.incrementAppOpenCount()
    }
}
