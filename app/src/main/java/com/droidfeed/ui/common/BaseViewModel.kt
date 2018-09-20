package com.droidfeed.ui.common

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private var lastClickTime: Long = 0
    private val job = Job()

    /**
     * Returns if a click event should be consumed or not. It is mainly to prevent spam clicks.
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

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}