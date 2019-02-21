package com.droidfeed.util

import android.app.Activity
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 * Util class for logging analytic events on Firebase Analytics.
 */
class AnalyticsUtil @Inject constructor(private val analytics: FirebaseAnalytics) {

    fun logBookmark(isBookmarked: Boolean) {
        analytics.logEvent(
            "bookmark",
            bundleOf(Pair("bookmarked", isBookmarked))
        )
    }

    fun logShare(content: String) {
        analytics.logEvent(
            FirebaseAnalytics.Event.SHARE,
            bundleOf(Pair("content", content))
        )
    }

    fun logPostShare() {
        logShare("post")
    }

    fun logPostClick() {
        analytics.logEvent(
            FirebaseAnalytics.Event.VIEW_ITEM,
            bundleOf(Pair("post", "post"))
        )
    }

    fun logScreenView(activity: Activity, screenTag: String) {
        analytics.setCurrentScreen(activity, screenTag, null)
    }

    fun logAppRateClick() {
        analytics.logEvent("open_play_store", null)
    }

    fun logAppRatePrompt() {
        analytics.logEvent("app_rate_prompt", null)
    }

    fun logNewsletterSignUp() {
        analytics.logEvent(
            FirebaseAnalytics.Event.SIGN_UP,
            bundleOf(Pair("newsletter", ""))
        )
    }
}
