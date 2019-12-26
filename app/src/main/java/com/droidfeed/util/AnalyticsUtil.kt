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
            bundleOf(Pair("bookmarked", isBookmarked.toString()))
        )
    }

    private fun logShare(content: String) {
        analytics.logEvent(
            FirebaseAnalytics.Event.SHARE,
            bundleOf(Pair("content", content))
        )
    }

    fun logPostShare() = logShare("post")
    fun logSourceShare() = logShare("source")

    fun logSourceActivation(isActivated: Boolean) {
        analytics.logEvent(
            "source",
            bundleOf(Pair("activated", isActivated.toString()))
        )
    }

    fun logPostClick() {
        analytics.logEvent(
            FirebaseAnalytics.Event.VIEW_ITEM,
            bundleOf(Pair("post", "post"))
        )
    }

    fun logConferenceClick() {
        analytics.logEvent(
            FirebaseAnalytics.Event.VIEW_ITEM,
            bundleOf(Pair("conference", "conference"))
        )
    }

    fun logCFPClick() {
        analytics.logEvent(
            FirebaseAnalytics.Event.VIEW_ITEM,
            bundleOf(Pair("conference", "CFP"))
        )
    }

    fun logAddSourceButtonClick() {
        analytics.logEvent("click_source_add", null)
    }

    fun logRemoveSourceButtonClick() {
        analytics.logEvent("click_source_remove", null)
    }

    fun logSourceRemoveUndo() {
        analytics.logEvent("click_source_remove_undo", null)
    }

    fun logSaveSourceButtonClick() {
        analytics.logEvent("click_save_source", null)
    }

    fun logScreenView(activity: Activity, screenTag: String) {
        analytics.setCurrentScreen(activity, screenTag, null)
    }

    fun logAppRateFromPromtClick() {
        analytics.logEvent("app_rate_prompt_rate", null)
    }

    fun logAppRatePrompt() {
        analytics.logEvent("app_rate_prompt", null)
    }

    fun logSourceAddSuccess() {
        analytics.logEvent("click_add_source_success", null)
    }

    fun logSourceAddFail() {
        analytics.logEvent("click_add_source_fail", null)
    }

    fun logSourceAlreadyExists() {
        analytics.logEvent("click_add_source_exists", null)
    }

    fun logSourceAddFailInvalidUrl() {
        analytics.logEvent("click_add_source_fail_invalid_url", null)
    }

}
