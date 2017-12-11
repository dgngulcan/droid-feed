package com.droidfeed.di

import com.droidfeed.di.fragment.MainFragmentModule
import com.droidfeed.ui.module.detail.ArticleDetailActivity
import com.droidfeed.ui.module.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Dogan Gulcan on 12/11/17.
 */
@Module
abstract class ActivityModule {

    @MainScope
    @ContributesAndroidInjector(modules = [MainFragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ArticleDetailScope
    @ContributesAndroidInjector
    abstract fun contributeArticleDetail(): ArticleDetailActivity

}