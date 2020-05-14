package com.droidfeed.di.activity

import androidx.appcompat.app.AppCompatActivity
import com.droidfeed.ui.module.onboard.OnBoardActivity
import dagger.Binds
import dagger.Module

@Module
abstract class OnBoardActivityModule {

    @Binds
    abstract fun bindActivity(activity: OnBoardActivity): AppCompatActivity

}