@file:Suppress("unused")

package com.droidfeed.di.activity

import androidx.appcompat.app.AppCompatActivity
import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.conferences.ConferencesFragment
import com.droidfeed.ui.module.contribute.ContributeFragment
import com.droidfeed.ui.module.feed.FeedFragment
import com.droidfeed.ui.module.main.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeNewsFragment(): FeedFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributeHelpUsFragment(): ContributeFragment

    @ContributesAndroidInjector
    abstract fun contributeConferencesFragment(): ConferencesFragment

    @Binds
    abstract fun bindActivity(activity: MainActivity): AppCompatActivity

}