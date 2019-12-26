package com.droidfeed.ui.module.about.analytics

import androidx.core.os.bundleOf
import com.droidfeed.ui.common.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AboutScreenLogger @Inject constructor(private val analytics: FirebaseAnalytics) {

    fun logAppRateClick() {
        analytics.logEvent("app_rate_click", getBundle())
    }

    fun logAppShareClick() {
        analytics.logEvent("app_share_click", getBundle())
    }

    fun logPrivacyPolicyClick() {
        analytics.logEvent("privacy_policy_click", getBundle())
    }

    fun logContactClick() {
        analytics.logEvent("contact_email_click", getBundle())
    }

    fun logLicencesClick() {
        analytics.logEvent("licences_click", getBundle())
    }

    private fun getBundle() = bundleOf(Pair(Analytics.SCREEN_ATTR, "about"))

}