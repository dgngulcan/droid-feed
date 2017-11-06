package com.droidfeed.ui.module.main

import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.about.AboutFragmentModule
import com.droidfeed.ui.module.news.NewsFragment
import com.droidfeed.ui.module.news.NewsFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module
abstract class MainFragmentModule {

    @ContributesAndroidInjector(modules = arrayOf(NewsFragmentModule::class))
    abstract fun contributeNewsFragment(): NewsFragment

    @ContributesAndroidInjector(modules = arrayOf(AboutFragmentModule::class))
    abstract fun contributeAboutFragment(): AboutFragment

}