@file:Suppress("unused")

package com.droidfeed.di.activity

import com.droidfeed.ui.module.about.license.LicensesActivity
import com.droidfeed.ui.module.main.MainActivity
import com.droidfeed.ui.module.onboard.OnBoardActivity
import com.droidfeed.ui.module.webview.WebViewActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [LicenseActivityModule::class])
    abstract fun contributeLicensesActivity(): LicensesActivity

    @ContributesAndroidInjector(modules = [OnBoardActivityModule::class])
    abstract fun contributeOnBoardActivity(): OnBoardActivity

    @ContributesAndroidInjector(modules = [WebViewActivityModule::class])
    abstract fun contributeWebViewActivity(): WebViewActivity

}