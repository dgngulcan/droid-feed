package com.droidfeed.ui.module.about

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.droidfeed.BuildConfig
import com.droidfeed.contactIntent
import com.droidfeed.rateAppIntent
import com.droidfeed.shareIntent
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import javax.inject.Inject

class AboutViewModel @Inject constructor() : BaseViewModel() {

    val startIntent = MutableLiveData<Event<Intent>>()
    val openUrl = MutableLiveData<Event<String>>()
    val openLicences = MutableLiveData<Event<Unit>>()

    fun openPlayStore() {
        startIntent.postValue(Event(rateAppIntent))
        analytics.logAppRateClick()
    }

    fun contactEmail() {
        startIntent.postValue(Event(contactIntent))
    }

    fun shareApp() {
        startIntent.postValue(Event(shareIntent))
        analytics.logShare("app")
    }

    fun openPrivacyPolicy() {
        openUrl.postValue(Event(BuildConfig.DROIDFEED_PRIVACY_POLICY))
    }

    fun openLicences() {
        openLicences.postValue(Event(Unit))
    }
}