package com.droidfeed.ui.module.about

import android.content.Intent
import com.droidfeed.BuildConfig
import com.droidfeed.contactIntent
import com.droidfeed.rateAppIntent
import com.droidfeed.shareIntent
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent
import javax.inject.Inject

class AboutViewModel @Inject constructor() : BaseViewModel() {

    val rateAppEvent = SingleLiveEvent<Intent>()
    val contactDevEvent = SingleLiveEvent<Intent>()
    val openLinkEvent = SingleLiveEvent<String>()
    val shareAppEvent = SingleLiveEvent<Intent>()
    val openLicencesEvent = SingleLiveEvent<Unit>()

    fun openPlayStore() {
        rateAppEvent.setValue(rateAppIntent)
    }

    fun contactEmail() {
        contactDevEvent.setValue(contactIntent)
    }

    fun shareApp() {
        shareAppEvent.setValue(shareIntent)
    }

    fun openPrivacyPolicy() {
        openLinkEvent.setValue(BuildConfig.DROIDFEED_PRIVACY_POLICY)
    }

    fun openLicences() {
        openLicencesEvent.setValue(Unit)
    }
}