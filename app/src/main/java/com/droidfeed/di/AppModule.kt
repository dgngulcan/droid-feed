package com.droidfeed.di

import android.content.Context
import com.droidfeed.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        (DatabaseModule::class),
        (ApiModule::class),
        (ViewModelModule::class)
    ]
)
class AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(app: App) =
        app.getSharedPreferences("df_sharedpreferences", Context.MODE_PRIVATE)
}