package com.droidfeed.ui.common

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private var lastClickTime: Long = 0
    private val job = Job()

    /**
     * Returns if a click event should be consumed or not. It is mainly to prevent spam clicks.
     */
    var canClick: Boolean = true
        get() {
            return if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_TIMEOUT_IN_MILLIS) {
                false
            } else {
                lastClickTime = SystemClock.elapsedRealtime()
                true
            }
        }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    companion object {
        private const val CLICK_TIMEOUT_IN_MILLIS = 200
    }
}