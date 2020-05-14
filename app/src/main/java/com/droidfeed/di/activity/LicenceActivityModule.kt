package com.droidfeed.di.activity

import androidx.appcompat.app.AppCompatActivity
import com.droidfeed.ui.module.about.licence.LicencesActivity
import dagger.Binds
import dagger.Module

@Module
abstract class LicenceActivityModule {

    @Binds
    abstract fun provideActivity(activity: LicencesActivity): AppCompatActivity

}