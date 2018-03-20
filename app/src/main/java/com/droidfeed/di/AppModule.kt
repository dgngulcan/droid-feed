package com.droidfeed.di

import android.content.Context
import com.droidfeed.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
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