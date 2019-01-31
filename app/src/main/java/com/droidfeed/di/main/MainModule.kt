package com.droidfeed.di.main

import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.util.ColorPalette
import com.droidfeed.util.CustomTab
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides
    fun providesCustomTab(activity: MainActivity) = CustomTab(activity)

    @Provides
    fun provideColorPalette(activity: MainActivity) = ColorPalette(activity)
}