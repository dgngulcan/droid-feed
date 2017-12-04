package com.droidfeed.di.main

import com.droidfeed.di.MainScope
import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.util.CustomTab
import dagger.Module
import dagger.Provides

/**
 * Created by Dogan Gulcan on 12/3/17.
 */
@Module
class MainModule {

    @Provides
    @MainScope
    fun providesCustomTab(mainActivity: MainActivity): CustomTab {
        return CustomTab(mainActivity)
    }

}