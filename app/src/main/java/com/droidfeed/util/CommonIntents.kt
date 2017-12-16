package com.droidfeed.util

import android.content.Intent
import android.net.Uri
import com.droidfeed.BuildConfig

/**
 * Created by Dogan Gulcan on 12/16/17.
 */

val contactIntent: Intent by lazy {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:hi.droidfeed@gmail.com")
    intent.putExtra(Intent.EXTRA_EMAIL, "hi.droidfeed@gmail.com")
    intent.putExtra(Intent.EXTRA_SUBJECT, "About DroidFeed")
}

val shareIntent: Intent by lazy {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, "This activity helps me to keep up with Android" +
            "\n\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
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


