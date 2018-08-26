package com.droidfeed.di

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import com.droidfeed.App
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.data.db.MIGRATION_1_2
import com.droidfeed.data.db.MIGRATION_2_3
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
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                insertSources(appDatabase)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                insertSources(appDatabase)
            }
        }).build()

        return appDatabase
    }

    private fun insertSources(appDatabase: AppDatabase?) {
        launch {
            appDatabase?.sourceDao()?.insertSources(
                listOf(
                    Source(
                        1,
                        "Android Dialogs",
                        "https://www.youtube.com/feeds/videos.xml?channel_id=UCMEmNnHT69aZuaOrE-dF6ug"
                    ),
                    Source(
                        2,
                        "AndroidPub",
                        "https://android.jlelse.eu/feed"
                    ),
                    Source(
                        3,
                        "ProAndroidDev",
                        "https://proandroiddev.com/feed"
                    ),
                    Source(
                        4,
                        "Google Developers",
                        "https://medium.com/feed/google-developers"
                    ),
                    Source(
                        5,
                        "Android Snacks",
                        "https://rss.simplecast.com/podcasts/3213/rss"
                    ),
                    Source(
                        6,
                        "AndroidDev",
                        "https://twitrss.me/twitter_user_to_rss/?user=AndroidDev"
                    ),
                    Source(
                        7,
                        "Android Developers Blog",
                        "https://www.blogger.com/feeds/6755709643044947179/posts/default?alt=rss&max-results=25"
                    ),
                    Source(
                        8,
                        "Kotlin Academy",
                        "https://blog.kotlin-academy.com/feed"
                    ),
                    Source(
                        9,
                        "Fragmented",
                        "https://fragmentedpodcast.com/feed"
                    )
                )
            )
        }
    }

    @Provides
    @Singleton
    fun providesRssDao(database: AppDatabase) = database.postDao()

    @Provides
    @Singleton
    fun providesSourceDao(database: AppDatabase) = database.sourceDao()
}