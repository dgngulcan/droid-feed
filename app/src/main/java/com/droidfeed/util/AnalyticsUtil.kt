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

    fun logShare() {
        analytics.logEvent(FirebaseAnalytics.Event.SHARE, null)
    }

    fun logArticleClick() {
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, null)
    }
}
