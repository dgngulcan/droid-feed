package com.droidfeed.di.activity

import androidx.appcompat.app.AppCompatActivity
import com.droidfeed.ui.module.webview.WebViewActivity
import dagger.Binds
import dagger.Module

@Module
abstract class WebViewActivityModule {

    @Binds
    abstract fun bindActivity(activity: WebViewActivity): AppCompatActivity
}