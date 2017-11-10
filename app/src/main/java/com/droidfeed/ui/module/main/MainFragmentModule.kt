package com.droidfeed.ui.module.main

import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.feed.FeedFragment
import com.droidfeed.ui.module.feed.FeedFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module
abstract class MainFragmentModule {

    @ContributesAndroidInjector(modules = arrayOf(FeedFragmentModule::class))
    abstract fun contributeNewsFragment(): FeedFragment

    @ContributesAndroidInjector()
    abstract fun contributeAboutFragment(): AboutFragment

}