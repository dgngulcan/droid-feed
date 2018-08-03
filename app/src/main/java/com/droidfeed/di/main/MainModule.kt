package com.droidfeed.di.main

import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.util.CustomTab
import dagger.Module
import dagger.Provides

/**
 * DI module for [MainActivity].
 */
@Module
class MainModule {

    @Provides
    fun providesCustomTab(activity: MainActivity): CustomTab {
        return CustomTab(activity)
    }
}