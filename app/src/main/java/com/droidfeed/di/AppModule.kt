package com.droidfeed.di

import android.content.Context
import android.content.SharedPreferences
import com.droidfeed.App
import com.droidfeed.util.ColorPalette
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences("df_sharedpreferences", Context.MODE_PRIVATE)

    @Provides
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideColorPalette(@ApplicationContext context: Context) = ColorPalette(context)
}