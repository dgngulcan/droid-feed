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
        sharedPrefs.canPromptRateApp = appRatePrompt
    }

    fun incrementItemShareCount() {
        sharedPrefs.shareCount += 1
    }

    fun incrementAppOpenCount() {
        sharedPrefs.appOpenCount += 1
    }

    var canPromptRateApp
        set(value) {
            sharedPrefs.canPromptRateApp = value
        }
        get() = sharedPrefs.canPromptRateApp

    var appRatePromptCount
        set(value) {
            sharedPrefs.appRatePromptIgnoreCount = value
        }
        get() = sharedPrefs.appRatePromptIgnoreCount

    var appOpenCount
        set(value) {
            sharedPrefs.appOpenCount = value
        }
        get() = sharedPrefs.appOpenCount

    var shareCount
        set(value) {
            sharedPrefs.shareCount = value
        }
        get() = sharedPrefs.shareCount

    var appRatePromptIndex
        set(value) {
            sharedPrefs.appRatePromptIndex = value
        }
        get() = sharedPrefs.appRatePromptIndex

    var hasAcceptedTerms
        set(value) {
            sharedPrefs.hasAcceptedTerms = value
        }
        get() = sharedPrefs.hasAcceptedTerms


}