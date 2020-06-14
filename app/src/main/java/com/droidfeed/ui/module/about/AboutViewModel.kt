package com.droidfeed.ui.module.about

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droidfeed.BuildConfig
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.event.Event

class AboutViewModel @ViewModelInject constructor() : ViewModel() {

    val startIntent = MutableLiveData<Event<IntentProvider.TYPE>>()
    val openUrl = MutableLiveData<Event<String>>()
    val openLicenses = MutableLiveData<Event<Unit>>()

    fun rateApp() {
        startIntent.postValue(Event(IntentProvider.TYPE.RATE_APP))
    }

    fun contactEmail() {
        startIntent.postValue(Event(IntentProvider.TYPE.CONTACT_EMAIL))
    }

    fun shareApp() {
        startIntent.postValue(Event(IntentProvider.TYPE.SHARE_APP))
    }

    fun openPrivacyPolicy() {
        openUrl.postValue(Event(BuildConfig.DROIDFEED_PRIVACY_POLICY))
    }

    fun openLicenses() {
        openLicenses.postValue(Event(Unit))
    }
}