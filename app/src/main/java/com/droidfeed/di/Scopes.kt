package com.droidfeed.di

import com.droidfeed.ui.module.main.MainActivity
import javax.inject.Scope

/**
 * Created by Dogan Gulcan on 12/3/17.
 */

/**
 * Scope of [MainActivity].
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MainScope
