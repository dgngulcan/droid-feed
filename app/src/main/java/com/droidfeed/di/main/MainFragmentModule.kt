package com.droidfeed.di.main

import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.feed.FeedFragment
import com.droidfeed.ui.module.contribute.ContributeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module
abstract class MainFragmentModule {

    @ContributesAndroidInjector()
    abstract fun contributeNewsFragment(): FeedFragment

    @ContributesAndroidInjector()
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector()
    abstract fun contributeHelpUsFragment(): ContributeFragment

}