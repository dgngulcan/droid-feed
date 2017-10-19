package com.droidnews.di

import com.droidnews.ui.module.main.MainActivity
import com.droidnews.ui.module.main.MainActivityModule
import com.droidnews.ui.module.main.MainFragmentModule
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