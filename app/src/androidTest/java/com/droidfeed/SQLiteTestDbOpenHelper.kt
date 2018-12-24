package com.droidfeed

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class SQLiteTestDbOpenHelper(
    context: Context,
    databaseName: String
) : SQLiteOpenHelper(
    context,
    databaseName,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `rss` (`bookmarked` INTEGER NOT NULL, `contentImage` TEXT NOT NULL, `link` TEXT NOT NULL, `pub_date` TEXT NOT NULL, `pub_date_timestamp` INTEGER NOT NULL, `title` TEXT NOT NULL, `author` TEXT NOT NULL, `content_raw` TEXT NOT NULL, `channel_title` TEXT NOT NULL, `channel_image_url` TEXT NOT NULL, `channel_link` TEXT NOT NULL, `content_image` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`link`))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }

    companion object {
        const val DATABASE_VERSION = 1
    }
}