package com.droidfeed.di

import androidx.room.Room
import com.droidfeed.App
import com.droidfeed.data.db.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(app: App) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        AppDatabase.APP_DATABASE_NAME
    ).addMigrations(
        MIGRATION_1_2,
        MIGRATION_1_4,
        MIGRATION_2_3,
        MIGRATION_3_4
    ).build()

    @Provides
    @Singleton
    fun providesRssDao(database: AppDatabase) = database.postDao()

    @Provides
    @Singleton
    fun providesSourceDao(database: AppDatabase) = database.sourceDao()
}