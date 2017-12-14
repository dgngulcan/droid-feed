package com.droidfeed.di

import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.util.CustomTab
import dagger.Module
import dagger.Provides

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module(includes = [(DatabaseModule::class), (ApiModule::class)])
class AppModule