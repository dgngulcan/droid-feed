package com.droidfeed.util.extention

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.droidfeed.util.logConsole
import com.droidfeed.util.logStackTrace
import org.xmlpull.v1.XmlPullParser
import java.util.Random

/**
 * Checks if the device has internet access.
 *
 * @return true if the device is online
 */
fun Context.isOnline(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun Intent.startActivity(activity: Activity) {
    if (this.resolveActivity(activity.packageManager) != null) {
        activity.startActivity(this)
    } else {
        logConsole("There are no activity can handle this intent")
    }
}

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

/**
 * Skips a tag within the parser.
 */
fun XmlPullParser.skip() {
    when {
        eventType != XmlPullParser.START_TAG -> return
        else -> try {
            var depth = 1
            while (depth != 0) {
                when (next()) {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        } catch (e: Exception) {
            logStackTrace(e)
        }
    }
}