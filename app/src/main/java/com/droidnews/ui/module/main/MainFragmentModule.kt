package com.droidnews.ui.module.main

import com.droidnews.ui.module.news.NewsFragment
import com.droidnews.ui.module.news.NewsFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module
abstract class MainFragmentModule {

    @ContributesAndroidInjector(modules = arrayOf(NewsFragmentModule::class))
    abstract fun contributeNewsFragment(): NewsFragment

}