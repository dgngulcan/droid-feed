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
import kotlinx.coroutines.experimental.launch
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(app: App): AppDatabase {
        var appDatabase: AppDatabase? = null
        appDatabase = Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.APP_DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    insertSources(appDatabase)
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    insertSources(appDatabase)
                }
            })
            .build()

        return appDatabase
    }

    private fun insertSources(appDatabase: AppDatabase?) {
        launch {
            appDatabase?.sourceDao()?.insertSources(
                listOf(
                    Source(
                        "Android Dialogs",
                        "https://www.youtube.com/feeds/videos.xml?channel_id=UCMEmNnHT69aZuaOrE-dF6ug"
                    ),
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
                        "AndroidDev",
                        "http://twitrss.me/twitter_user_to_rss/?user=AndroidDev"
                    ),
                    Source(
                        "Android Developers Blog",
                        "https://www.blogger.com/feeds/6755709643044947179/posts/default?alt=rss&max-results=25"
                    ),
                    Source(
                        "Kotlin Academy",
                        "https://blog.kotlin-academy.com/feed"
                    ),
                    Source(
                        "Fragmented",
                        "http://fragmentedpodcast.com/feed"
                    )
                )
            )
        }
    }

    @Provides
    @Singleton
    fun providesRssDao(database: AppDatabase) = database.rssDao()

    @Provides
    @Singleton
    fun providesSourceDao(database: AppDatabase) = database.sourceDao()
}