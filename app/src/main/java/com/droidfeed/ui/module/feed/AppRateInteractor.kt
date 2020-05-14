package com.droidfeed.ui.module.feed

import com.droidfeed.data.repo.SharedPrefsRepo
import javax.inject.Inject


class AppRateInteractor @Inject constructor(
    private val sharedPrefs: SharedPrefsRepo
) {

    fun isFitForAppRatePrompt(bookmarkCount: Int): Boolean {
        val openCountMatches = sharedPrefs.appOpenCount > sharedPrefs.appRatePromptIndex
        val bookmarkCountMatches = bookmarkCount > sharedPrefs.appRatePromptIndex
        val shareCountMatches = sharedPrefs.shareCount > sharedPrefs.appRatePromptIndex

        return sharedPrefs.canPromptRateApp && openCountMatches && (bookmarkCountMatches || shareCountMatches)
    }

}
