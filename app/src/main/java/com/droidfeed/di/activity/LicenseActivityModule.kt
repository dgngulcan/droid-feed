package com.droidfeed.di.activity

import androidx.appcompat.app.AppCompatActivity
import com.droidfeed.ui.module.about.license.LicensesActivity
import dagger.Binds
import dagger.Module

@Module
abstract class LicenseActivityModule {

    @Binds
    abstract fun bindActivity(activity: LicensesActivity): AppCompatActivity

}