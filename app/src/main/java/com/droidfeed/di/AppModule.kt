package com.droidfeed.di

import com.droidfeed.App
import com.droidfeed.util.CustomTab
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module(includes = [(DatabaseModule::class), (ApiModule::class)])
class AppModule {

    @Singleton
    @Provides
    fun providesCustomTab(app: App): CustomTab {
        return CustomTab(app)
    }
}