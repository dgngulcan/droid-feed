package com.droidfeed

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.droidfeed.di.DaggerAppComponent
import com.droidfeed.util.appOpenCount
import com.droidfeed.util.logThrowable
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Suppress("unused")
    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate() {
        super.onCreate()
        initDagger()

        initSources()

        sharedPrefs.appOpenCount += 1
    }

    private fun initSources() {
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

/**
 * Intent for sending an email for contact purposes.
 */
val contactIntent: Intent by lazy {
    Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:hi@droidfeed.io")
        putExtra(Intent.EXTRA_EMAIL, "hi@droidfeed.io")
        putExtra(Intent.EXTRA_SUBJECT, "About DroidFeed")
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
}

/**
 * Intent for sharing the application.
 */
val shareIntent: Intent by lazy {
    Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out DroidFeed! News feed for Android Developers" +
                    "\n\nhttps://play.google.com/store/apps/details?id=" +
                    "${BuildConfig.APPLICATION_ID}&referrer=utm_source%3Din_app_share"
        )
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }
}

/**
 * Intent for the applications Google play store page.
 */
val rateAppIntent: Intent by lazy {
    try {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        )
    } catch (e: android.content.ActivityNotFoundException) {
        logThrowable(e)
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )
        )
    }
}