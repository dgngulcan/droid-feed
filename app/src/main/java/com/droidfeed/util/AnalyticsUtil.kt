package com.droidfeed.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsUtil @Inject constructor(private val analytics: FirebaseAnalytics) {

    fun logBookmark(isBookmarked: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean("bookmarked", isBookmarked)
        analytics.logEvent("bookmark", bundle)
    }

    fun logPostShare() {
        val bundle = Bundle()
        bundle.putString("content", "post")
        analytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    fun logPostClick() {
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, null)
    }
}
