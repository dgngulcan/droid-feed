package com.droidfeed.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.droidfeed.data.db.AppDatabase.Companion.SOURCE_TABLE_NAME


/**
 * Created by Dogan Gulcan on 1/25/18.
 */

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `$SOURCE_TABLE_NAME` (`url` TEXT, `name` TEXT, `is_active` INTEGER, PRIMARY KEY(`url`))")
        database.execSQL("INSERT INTO `$SOURCE_TABLE_NAME` (NAME,URL,IS_ACTIVE) VALUES('https://android.jlelse.eu/feed', 'AndroidPub', 1)")
        database.execSQL("INSERT INTO `$SOURCE_TABLE_NAME` (NAME,URL,IS_ACTIVE) VALUES('https://proandroiddev.com/feed', 'ProAndroidDev', 1)")
        database.execSQL("INSERT INTO `$SOURCE_TABLE_NAME` (NAME,URL,IS_ACTIVE) VALUES('https://medium.com/feed/google-developers', 'Google Developers', 1)")
        database.execSQL("INSERT INTO `$SOURCE_TABLE_NAME` (NAME,URL,IS_ACTIVE) VALUES('https://rss.simplecast.com/podcasts/3213/rss', 'Android Snacks', 1)")
        database.execSQL("INSERT INTO `$SOURCE_TABLE_NAME` (NAME,URL,IS_ACTIVE) VALUES('http://androidbackstage.blogspot.com/feeds/posts/default?alt=rss', 'Android Developers Backstage', 1)")
        database.execSQL("INSERT INTO `$SOURCE_TABLE_NAME` (NAME,URL,IS_ACTIVE) VALUES('http://fragmentedpodcast.com/feed', 'Fragmented', 1)")
        database.close()
    }
}


