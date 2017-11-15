package com.droidfeed.util.extention

import android.content.res.Resources

/**
 * Created by Dogan Gulcan on 11/8/17.
 */

val Int.asPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.asDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

