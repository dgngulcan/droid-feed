package com.droidfeed.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source

@Database(
    entities = [(Source::class),
        (Post::class)],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val POST_TABLE = "rss"
        const val SOURCE_TABLE = "source"
        const val APP_DATABASE_NAME = "droidfeed.db"
    }

    abstract fun postDao(): PostDao

    abstract fun sourceDao(): SourceDao
}