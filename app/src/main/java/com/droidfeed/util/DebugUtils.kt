package com.droidfeed.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.droidfeed.BuildConfig
import java.lang.Exception

private const val TAG = "DroidFeed"

/**
 * Exception logger.
 */
fun logStackTrace(exception: Exception, message: String = "Exception") {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message, exception)
        exception.printStackTrace()
    } else {
        Crashlytics.logException(exception)
    }
}

/**
 * Debug console logger.
 */
fun logConsole(message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}