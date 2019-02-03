package com.droidfeed.util.extention

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager


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

/**
 * Checks if the given package name is available on the device.
 *
 * @param targetPackage package name to be checked
 * @return true if available
 */
fun Context.isPackageAvailable(targetPackage: String): Boolean {
    return try {
        packageManager.getPackageInfo(targetPackage, PackageManager.GET_META_DATA)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}