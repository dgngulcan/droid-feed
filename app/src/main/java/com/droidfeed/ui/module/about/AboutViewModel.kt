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
    val openLibrariesEvent = SingleLiveEvent<Unit>()

    fun openPlayStore() {
        if (canClick) rateAppEvent.setValue(rateAppIntent)
    }

    fun contactEmail() {
        if (canClick) contactDevEvent.setValue(contactIntent)
    }

    fun shareApp() {
        if (canClick) shareAppEvent.setValue(shareIntent)
    }

    fun openPrivacyPolicy() {
        if (canClick) openLinkEvent.setValue(BuildConfig.DROIDFEED_PRIVACY_POLICY)
    }

    fun openLicences() {
        if (canClick) openLibrariesEvent.setValue(Unit)
    }
}