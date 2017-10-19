package com.droidnews.di

import android.app.Application
import com.droidnews.data.api.ApiModule
import com.droidnews.data.db.DatabaseModule
import com.droidnews.ui.binding.DataBindingModule
import com.droidnews.util.UtilsModule
import dagger.Module

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module(includes = arrayOf(
        UtilsModule::class,
        DatabaseModule::class,
        DataBindingModule::class,
        ApiModule::class))
class AppModule(private val application: Application)