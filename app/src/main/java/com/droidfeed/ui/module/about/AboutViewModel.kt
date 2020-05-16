package com.droidfeed.ui.module.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droidfeed.BuildConfig
import com.droidfeed.ui.module.about.analytics.AboutScreenLogger
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.event.Event
import javax.inject.Inject

class AboutViewModel @Inject constructor(private val logger: AboutScreenLogger) : ViewModel() {

    val startIntent = MutableLiveData<Event<IntentProvider.TYPE>>()
    val openUrl = MutableLiveData<Event<String>>()
    val openLicenses = MutableLiveData<Event<Unit>>()

    fun rateApp() {
        startIntent.postValue(Event(IntentProvider.TYPE.RATE_APP))
        logger.logAppRateClick()
    }

    fun contactEmail() {
        startIntent.postValue(Event(IntentProvider.TYPE.CONTACT_EMAIL))
        logger.logContactClick()
    }

    fun shareApp() {
        startIntent.postValue(Event(IntentProvider.TYPE.SHARE_APP))
        logger.logAppShareClick()
    }

    fun openPrivacyPolicy() {
        openUrl.postValue(Event(BuildConfig.DROIDFEED_PRIVACY_POLICY))
        logger.logPrivacyPolicyClick()
    }

    fun openLicenses() {
        openLicenses.postValue(Event(Unit))
        logger.logLicensesClick()
    }
}