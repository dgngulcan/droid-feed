package com.droidfeed.ui.module.feed.analytics

import androidx.core.os.bundleOf
import com.droidfeed.ui.common.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedScreenLogger @Inject constructor(private val analytics: FirebaseAnalytics) {

    fun logPostClick() {
        analytics.logEvent("feed_item_click", getBundle())
    }

    fun logPostShare() {
        analytics.logEvent("feed_item_share", getBundle())
    }

    fun logBookmark(isBookmarked: Boolean) {
        analytics.logEvent(
            "feed_item_bookmark",
            bundleOf(Pair("bookmarked", isBookmarked.toString()))
        )
    }

    fun logAppRatePrompt() {
        analytics.logEvent("app_rate_prompt_rate", getBundle())
    }

    private fun getBundle() = bundleOf(Pair(Analytics.SCREEN_ATTR, "feed"))
    fun logAppRateFromPromtClick() {


    }

}