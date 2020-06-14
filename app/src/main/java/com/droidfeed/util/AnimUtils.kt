package com.droidfeed.util

import android.content.Context
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimUtils @Inject constructor(@ApplicationContext appContext: Context) {

    val fastOutSlowInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.fast_out_slow_in)
    }

    val fastOutLinearInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.fast_out_linear_in)
    }

    val linearOutSlowInInterpolator: Interpolator by lazy {
        AnimationUtils.loadInterpolator(appContext, android.R.interpolator.linear_out_slow_in)
    }

    companion object {
        const val MEDIUM_ANIM_DURATION = 500L
        const val SHORT_ANIM_DURATION = 150L
    }
}