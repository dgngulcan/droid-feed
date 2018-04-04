package com.droidfeed.util

import android.content.Intent
import android.net.Uri
import com.droidfeed.BuildConfig

/**
 * Created by Dogan Gulcan on 12/16/17.
 */

val contactIntent: Intent by lazy {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:hi@droidfeed.io")
    intent.putExtra(Intent.EXTRA_EMAIL, "hi@droidfeed.io")
    intent.putExtra(Intent.EXTRA_SUBJECT, "About DroidFeed")
}

val shareIntent: Intent by lazy {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, "Check out this app! It helps me stay up to date with Android development news." +
            "\n\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}&referrer=utm_source%3Din_app_share")
    intent.type = "text/plain"
    intent
}

val rateAppIntent: Intent by lazy {
    try {
        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))
    } catch (e: android.content.ActivityNotFoundException) {
        DebugUtils.showStackTrace(e)
        Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))
    }
}


