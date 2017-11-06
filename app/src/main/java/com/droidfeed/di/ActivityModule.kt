package com.droidfeed.di

import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.ui.module.main.MainActivityModule
import com.droidfeed.ui.module.main.MainFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = arrayOf(
            MainActivityModule::class,
            MainFragmentModule::class))
    abstract fun contributeMainActivity(): MainActivity

}