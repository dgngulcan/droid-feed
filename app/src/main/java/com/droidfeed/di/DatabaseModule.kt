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
import kotlinx.coroutines.experimental.async
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesAppDatabase(app: App): AppDatabase {
        var appDatabase: AppDatabase? = null
        appDatabase = Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.APP_DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    appDatabase?.sourceDao()?.insertSources(
                        listOf(
                            Source("AndroidPub", "https://android.jlelse.eu/feed", true),
                            Source("ProAndroidDev", "https://proandroiddev.com/feed", true),
                            Source(
                                "Google Developers",
                                "https://medium.com/feed/google-developers", true
                            ),
                            Source(
                                "Android Snacks",
                                "https://rss.simplecast.com/podcasts/3213/rss", true
                            ),
                            Source(
                                "Android Developers Backstage",
                                "http://androidbackstage.blogspot.com/feeds/posts/default?alt=rss",
                                true
                            ), // droid snacks
                            Source("Fragmented", "http://fragmentedpodcast.com/feed", true)
                        )
                    )
                }
            })
            .build()

        return appDatabase
    }

    @Singleton
    @Provides
    fun providesRssDao(database: AppDatabase) = database.rssDao()

    @Singleton
    @Provides
    fun providesSourceDao(database: AppDatabase) = database.sourceDao()

}