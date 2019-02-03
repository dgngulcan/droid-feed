package com.droidfeed.di

import com.droidfeed.di.main.MainFragmentModule
import com.droidfeed.di.main.MainModule
import com.droidfeed.ui.module.about.licence.LicencesActivity
import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.ui.module.onboard.OnBoardActivity
import com.droidfeed.ui.module.webview.WebViewActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @MainScope
    @ContributesAndroidInjector(modules = [MainFragmentModule::class, MainModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeLicencesActivity(): LicencesActivity

    @ContributesAndroidInjector
    abstract fun contributeOnBoardActivity(): OnBoardActivity

    @ContributesAndroidInjector
    abstract fun contributeWebViewActivity(): WebViewActivity
}