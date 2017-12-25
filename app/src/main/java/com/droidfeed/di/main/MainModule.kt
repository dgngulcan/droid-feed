package com.droidfeed.di.main

import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.util.CustomTab
import dagger.Module
import dagger.Provides

/**
 * Created by Dogan Gulcan on 12/12/17.
 */
@Module
class MainModule {

    @Provides
    fun providesCustomTab(activity: MainActivity): CustomTab {
        return CustomTab(activity)
    }
}