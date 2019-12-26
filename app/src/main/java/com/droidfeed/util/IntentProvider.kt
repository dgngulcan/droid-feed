package com.droidfeed.util

import android.content.Intent
import android.net.Uri
import com.droidfeed.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class IntentProvider @Inject constructor() {

    /**
     * Intent for sending an email for contact purposes.
     */
    private val contactIntent: Intent by lazy {
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
    private val shareIntent: Intent by lazy {
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
    private val rateAppIntent: Intent by lazy {
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

    fun getIntent(type: TYPE) = when (type) {
        TYPE.SHARE_APP -> shareIntent
        TYPE.RATE_APP -> rateAppIntent
        TYPE.CONTACT_EMAIL -> contactIntent
    }

    enum class TYPE {
        SHARE_APP,
        RATE_APP,
        CONTACT_EMAIL
    }
}
