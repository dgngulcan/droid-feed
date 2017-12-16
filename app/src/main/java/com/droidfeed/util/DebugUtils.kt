package com.droidfeed.util

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.droidfeed.BuildConfig
import java.lang.Exception

@Suppress("ConstantConditionIf")
/**
 * Created by Dogan Gulcan on 10/26/17.
 */
class DebugUtils {

    companion object {
        fun showStackTrace(e: Exception, message: String = "Exception") {
            if (BuildConfig.DEBUG_MODE) {
                Log.e("DebugLog", message, e)
                e.printStackTrace()
            }

            Crashlytics.logException(e)
        }

        fun log(s: String) {
            if (BuildConfig.DEBUG_MODE) {
                Log.e("DebugLog", s)
            }
        }
    }

}