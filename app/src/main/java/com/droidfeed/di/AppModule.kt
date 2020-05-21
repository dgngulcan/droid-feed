package com.droidfeed.di

import android.content.Context
import android.content.SharedPreferences
import com.droidfeed.App
import com.droidfeed.util.ColorPalette
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        DatabaseModule::class,
        ApiModule::class,
        FirebaseModule::class,
        ViewModelModule::class
    ]
)
class AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(app: App): SharedPreferences =
        app.getSharedPreferences("df_sharedpreferences", Context.MODE_PRIVATE)

    @Provides
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideColorPalette(context: App) = ColorPalette(context)
}