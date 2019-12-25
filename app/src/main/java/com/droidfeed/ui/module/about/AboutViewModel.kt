package com.droidfeed.ui.module.about

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.droidfeed.BuildConfig
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.event.Event
import javax.inject.Inject

class AboutViewModel @Inject constructor(
    private val intentProvider: IntentProvider
) : BaseViewModel() {

    val startIntent = MutableLiveData<Event<Intent>>()
    val openUrl = MutableLiveData<Event<String>>()
    val openLicences = MutableLiveData<Event<Unit>>()

    fun openPlayStore() {
        startIntent.postValue(Event(intentProvider.rateAppIntent))
        analytics.logAppRateClick()
    }

    fun contactEmail() {
        startIntent.postValue(Event(intentProvider.contactIntent))
    }

    fun shareApp() {
        startIntent.postValue(Event(intentProvider.shareIntent))
        analytics.logShareApp()
    }

    fun openPrivacyPolicy() {
        openUrl.postValue(Event(BuildConfig.DROIDFEED_PRIVACY_POLICY))
    }

    fun openLicences() {
        openLicences.postValue(Event(Unit))
    }
}