package com.droidfeed.di

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.droidfeed.App
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.data.db.MIGRATION_1_2
import com.droidfeed.data.model.Source
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.coroutines.experimental.bg
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Module
class DatabaseModule {

    //    @Singleton
    @Provides
    fun providesAppDatabase(app: App): AppDatabase {
        var appDatabase: AppDatabase? = null
        appDatabase = Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.APP_DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                insertSources(appDatabase)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                insertSources(appDatabase)
            }
        })
            .addMigrations(MIGRATION_1_2)
            .build()

        return appDatabase
    }

    private fun insertSources(appDatabase: AppDatabase?) {
        bg {
            appDatabase?.sourceDao()?.insertSources(
                listOf(
                    Source(
                        "AndroidPub",
                        "https://android.jlelse.eu/feed"
                    ),
                    Source(
                        "ProAndroidDev",
                        "https://proandroiddev.com/feed"
                    ),
                    Source(
                        "Google Developers",
                        "https://medium.com/feed/google-developers"
                    ),
                    Source(
                        "Android Snacks",
                        "https://rss.simplecast.com/podcasts/3213/rss"
                    ),
                    Source(
                        "Fragmented",
                        "http://fragmentedpodcast.com/feed"
                    )
                )
            )
        }
    }

    @Singleton
    @Provides
    fun providesRssDao(database: AppDatabase) = database.rssDao()

    @Singleton
    @Provides
    fun providesSourceDao(database: AppDatabase) = database.sourceDao()

}