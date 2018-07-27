package com.droidfeed

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import com.droidfeed.di.DaggerAppComponent
import com.droidfeed.util.appOpenCount
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        initDagger()

        sharedPrefs.appOpenCount += 1
    }

    private fun initDagger() {
        DaggerAppComponent
            .builder()
            .application(this)
            .build()
            .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}