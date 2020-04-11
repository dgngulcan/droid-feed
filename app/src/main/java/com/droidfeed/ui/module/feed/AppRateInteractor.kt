package com.droidfeed.ui.module.feed

import com.droidfeed.data.repo.SharedPrefsRepo
import javax.inject.Inject


class AppRateInteractor @Inject constructor(
    private val sharedPrefs: SharedPrefsRepo
) {

    fun canShowAppRate() = sharedPrefs.appRatePrompt

    fun isFitForAppRatePrompt(bookmarkCount: Int): Boolean {
        return sharedPrefs.appOpenCount > sharedPrefs.appRatePromptIndex &&
                (bookmarkCount > sharedPrefs.appRatePromptIndex ||
                        sharedPrefs.shareCount > sharedPrefs.appRatePromptIndex)

    }


}
