package com.droidfeed.util

import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.droidfeed.App
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimUtils @Inject constructor(appContext: App) {

    val fastOutSlowInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.fast_out_slow_in)
    }

    val fastOutLinearInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.fast_out_linear_in)
    }

    val linearOutSlowInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.linear_out_slow_in)
    }
}