package com.droidfeed.util

import android.app.Activity
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 * Util class for logging analytic events on Firebase Analytics.
 */
class AnalyticsUtil @Inject constructor(private val analytics: FirebaseAnalytics) {

    /**
     * Logs post bookmarking events.
     *
     * @param isBookmarked
     */
    fun logBookmark(isBookmarked: Boolean) {
        analytics.logEvent(
            "bookmark",
            bundleOf(Pair("bookmarked", isBookmarked))
        )
    }

    /**
     * Logs share events.
     *
     * @param content
     */
    fun logShare(content: String) {
        analytics.logEvent(
            FirebaseAnalytics.Event.SHARE,
            bundleOf(Pair("content", content))
        )
    }

    /**
     * Logs post click events.
     */
    fun logPostClick() {
        analytics.logEvent(
            FirebaseAnalytics.Event.VIEW_ITEM,
            bundleOf(Pair("post", "post"))
        )
    }

    /**
     * Logs screen view events.
     *
     * @param activity
     * @param screenTag
     */
    fun logScreenView(activity: Activity, screenTag: String) {
        analytics.setCurrentScreen(activity, screenTag, null)
    }

    /**
     * Logs app rate events.
     */
    fun logAppRateClick() {
        analytics.logEvent("open_play_store", null)
    }

    /**
     * Logs newsletter sign up events.
     */
    fun logNewsletterSignUp() {
        analytics.logEvent(
            FirebaseAnalytics.Event.SIGN_UP,
            bundleOf(Pair("newsletter", ""))
        )
    }
}
