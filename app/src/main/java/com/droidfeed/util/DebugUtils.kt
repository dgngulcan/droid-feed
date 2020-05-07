package com.droidfeed.util

import android.util.Log
import com.droidfeed.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics

private const val TAG = "DroidFeed"

/**
 * Exception logger.
 *
 * @param throwable
 */
fun logThrowable(throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        throwable.printStackTrace()
    } else {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}

/**
 * Debug console logger.
 *
 * @param message
 */
fun logConsole(message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}