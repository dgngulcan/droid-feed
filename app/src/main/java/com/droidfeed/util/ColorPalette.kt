package com.droidfeed.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.droidfeed.R
import javax.inject.Inject

class ColorPalette @Inject constructor(context: Context) {

    val transparent by lazy { ContextCompat.getColor(context, R.color.transparent) }
    val accent by lazy { ContextCompat.getColor(context, R.color.colorAccent) }
    val white by lazy { ContextCompat.getColor(context, android.R.color.white) }
    val black by lazy { ContextCompat.getColor(context, android.R.color.black) }
    val pink by lazy { ContextCompat.getColor(context, R.color.pink) }
    val blue by lazy { ContextCompat.getColor(context, R.color.blue) }
    val gray by lazy { ContextCompat.getColor(context, R.color.gray) }
    val grayDark by lazy { ContextCompat.getColor(context, R.color.grayDark1) }
}