package com.droidfeed.ui.module.conferences.analytics

import androidx.core.os.bundleOf
import com.droidfeed.ui.common.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class ConferencesScreenLogger @Inject constructor(private val analytics: FirebaseAnalytics) {

    fun logConferenceClick() {
        analytics.logEvent("conference_click", getBundle())
    }

    fun logCFPClick() {
        analytics.logEvent("cfp_click", getBundle())
    }

    private fun getBundle() = bundleOf(Pair(Analytics.SCREEN_ATTR, "conferences"))

}
