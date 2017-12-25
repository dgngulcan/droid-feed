package com.droidfeed.di

import android.arch.persistence.room.Room
import com.droidfeed.App
import com.droidfeed.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(app: App): AppDatabase =
            Room.databaseBuilder(app,
                    AppDatabase::class.java,
                    AppDatabase.APP_DATABASE_NAME)
                    .build()

    @Singleton
    @Provides
    fun providesRssDao(database: AppDatabase) = database.rssDao()

}