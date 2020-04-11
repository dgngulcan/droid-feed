package com.droidfeed.data.repo

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsRepo @Inject constructor(private val sharedPrefs: SharedPreferences) {

    fun incrementAppRatePromptIgnoreCount() {
        sharedPrefs.appRatePromptIgnoreCount += 1
    }

    fun incrementAppRatePromptIndex() {
        sharedPrefs.appRatePromptIndex += APP_RATE_PROMPT_INDEX * 3
    }

    fun setAppRatePrompt(appRatePrompt: Boolean) {
        sharedPrefs.appRatePrompt = appRatePrompt
    }

    fun setHasAcceptedTerms(hasAccepted: Boolean) {
        sharedPrefs.hasAcceptedTerms = hasAccepted
    }

    fun incrementItemShareCount() {
        sharedPrefs.shareCount += 1
    }

    fun incrementAppOpenCount() {
        sharedPrefs.appOpenCount += 1
    }

    val appRatePrompt = sharedPrefs.appRatePrompt
    val appRatePromptCount = sharedPrefs.appRatePromptIgnoreCount
    val appOpenCount = sharedPrefs.appOpenCount
    val shareCount = sharedPrefs.shareCount
    val appRatePromptIndex = sharedPrefs.appRatePromptIndex
    val hasAcceptedTerms = sharedPrefs.hasAcceptedTerms


}