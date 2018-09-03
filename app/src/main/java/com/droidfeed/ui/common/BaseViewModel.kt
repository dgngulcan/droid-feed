package com.droidfeed.ui.common

import androidx.lifecycle.ViewModel
import android.os.SystemClock

abstract class BaseViewModel : ViewModel() {

    private var lastClickTime: Long = 0

    /**
     * To prevent click spams.
     */
    var canClick: Boolean = true
        get() {
            return if (SystemClock.elapsedRealtime() - lastClickTime < 250) {
                false
            } else {
                lastClickTime = SystemClock.elapsedRealtime()
                true
            }
        }
}