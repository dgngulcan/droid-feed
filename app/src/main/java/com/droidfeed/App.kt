package com.droidfeed

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.droidfeed.di.DaggerAppComponent
import com.droidfeed.util.appOpenCount
import com.droidfeed.util.logStackTrace
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

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

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

val contactIntent: Intent by lazy {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:hi@droidfeed.io")
    intent.putExtra(Intent.EXTRA_EMAIL, "hi@droidfeed.io")
    intent.putExtra(Intent.EXTRA_SUBJECT, "About DroidFeed")
}

val shareIntent: Intent by lazy {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(
        Intent.EXTRA_TEXT,
        "Check out DroidFeed! News feed for Android Developers" +
                "\n\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}&referrer=utm_source%3Din_app_share"
    )
    intent.type = "text/plain"
    intent
}

val rateAppIntent: Intent by lazy {
    try {
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))
    } catch (e: android.content.ActivityNotFoundException) {
        logStackTrace(e)
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        )
    }
}