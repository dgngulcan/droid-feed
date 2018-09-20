package com.droidfeed.util.extention

import android.app.Activity
import android.content.Intent
import com.droidfeed.util.logConsole

/**
 * Starts an activity if the activity can be resolved.
 *
 * @param activity
 */
fun Intent.startActivity(activity: Activity) {
    if (this.resolveActivity(activity.packageManager) != null) {
        activity.startActivity(this)
    } else {
        logConsole("There are no activity can handle this intent")
    }
}