package com.droidfeed.util

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Created by Dogan Gulcan on 3/17/18.
 */

internal var SharedPreferences.shareCount: Int
    set(value) {
        edit { putInt("share_count", value) }
    }
    get() {
        return getInt("share_count", 0)
    }

internal var SharedPreferences.appOpenCount: Int
    set(value) {
        edit { putInt("app_open_count", value) }
    }
    get() {
        return getInt("app_open_count", 0)
    }

internal var SharedPreferences.appRatePromptIndex: Int
    set(value) {
        edit { putInt("app_rate_prompt_count", value) }
    }
    get() {
        return getInt("app_rate_prompt_count", APP_RATE_PROMPT_INDEX)
    }


internal var SharedPreferences.appRatePrompt: Boolean
    set(value) {
        edit { putBoolean("app_rate_prompt", value) }
    }
    get() {
        return getBoolean("app_rate_prompt", true)
    }

