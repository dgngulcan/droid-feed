package com.droidfeed.ui.common

import android.arch.lifecycle.ViewModel
import android.os.SystemClock

abstract class BaseViewModel : ViewModel() {

    private var mLastClickTime: Long = 0

    /**
     * To prevent click spams.
     */
    var userCanClick: Boolean = true
        get() {
            return if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                false
            } else {
                mLastClickTime = SystemClock.elapsedRealtime()
                true
            }
        }
}