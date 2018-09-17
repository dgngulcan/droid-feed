package com.droidfeed.util

import android.os.Build

/**
 * Returns true if the device is running Marshmallow or higher OS version.
 */
fun isMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

/**
 * Returns true if the device is running Oreo or higher OS version.
 */
fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O