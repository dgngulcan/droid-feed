package com.droidfeed.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Source


/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Database(entities = [(Source::class), (Article::class)], version = 2)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val RSS_TABLE_NAME = "rss"
        const val SOURCE_TABLE_NAME = "source"
        const val APP_DATABASE_NAME = "droidfeed.db"
    }

    abstract fun rssDao(): RssDao

    abstract fun sourceDao(): SourceDao

}


