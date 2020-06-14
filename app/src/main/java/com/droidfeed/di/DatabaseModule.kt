package com.droidfeed.di

import android.content.Context
import androidx.room.Room
import com.droidfeed.data.db.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        AppDatabase.APP_DATABASE_NAME
    ).addMigrations(
        MIGRATION_1_2,
        MIGRATION_1_4,
        MIGRATION_1_5,
        MIGRATION_2_3,
        MIGRATION_2_5,
        MIGRATION_3_4,
        MIGRATION_3_5,
        MIGRATION_4_5
    ).build()

    @Provides
    @Singleton
    fun providesRssDao(database: AppDatabase) = database.postDao()

    @Provides
    @Singleton
    fun providesSourceDao(database: AppDatabase) = database.sourceDao()
}