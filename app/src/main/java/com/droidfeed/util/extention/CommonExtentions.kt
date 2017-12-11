package com.droidfeed.util.extention

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager

/**
 * Created by Dogan Gulcan on 11/8/17.
 */

val Int.asPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.asDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()



fun Context.isOnline(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}