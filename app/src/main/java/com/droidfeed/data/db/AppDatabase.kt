package com.droidfeed.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.droidfeed.data.model.Article

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Database(entities = [(Article::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val RSS_TABLE_NAME = "rss"
        const val APP_DATABASE_NAME = "droidfeed.db"
    }

    abstract fun rssDao(): RssDao

}