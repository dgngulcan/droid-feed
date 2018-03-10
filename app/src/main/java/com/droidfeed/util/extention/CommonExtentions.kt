package com.droidfeed.util.extention

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import com.droidfeed.util.DebugUtils
import java.util.*

/**
 * Created by Dogan Gulcan on 11/8/17.
 */

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Context.isOnline(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun Intent.startActivity(activity: Activity) {
    if (this.resolveActivity(activity.packageManager) != null) {
        activity.startActivity(this)
    } else {
        DebugUtils.log("There are no activity can handle this intent")
    }
}

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start