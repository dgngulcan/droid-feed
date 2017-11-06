package com.droidfeed.util

import android.util.Property
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import com.droidfeed.App
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 11/5/17.
 */
@Singleton
class AnimUtils @Inject constructor(appContext: App) {

    val fastOutSlowInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.fast_out_slow_in)
    }

    val fastOutLinearInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.fast_out_linear_in)
    }

    val getLinearOutSlowInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.linear_out_slow_in)
    }

}