package com.nytclient.ui.common

import android.arch.lifecycle.ViewModel
import android.os.SystemClock


/**
 * Created by Dogan Gulcan on 9/12/17.
 */
abstract class BaseViewModel : ViewModel() {

    private var mLastClickTime: Long = 0

    var canUserClick: Boolean = true
        get() {
            return if (SystemClock.elapsedRealtime() - mLastClickTime < 300) {
                false
            } else {
                mLastClickTime = SystemClock.elapsedRealtime()
                true
            }
        }


}