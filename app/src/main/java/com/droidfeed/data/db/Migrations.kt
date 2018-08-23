package com.droidfeed.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.droidfeed.data.db.AppDatabase.Companion.POST_TABLE_NAME
import com.droidfeed.data.db.AppDatabase.Companion.SOURCE_TABLE_NAME

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `$SOURCE_TABLE_NAME` (`url` TEXT NOT NULL, `name` TEXT NOT NULL, `is_active` INTEGER NOT NULL, PRIMARY KEY(`url`))")
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // add `id` to source table and make it primary key
        database.execSQL("CREATE TABLE IF NOT EXISTS `source_temp` (`url` TEXT NOT NULL, `name` TEXT NOT NULL, `is_active` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        database.execSQL("INSERT INTO source_temp (url, name, is_active) SELECT url, name, is_active FROM `$SOURCE_TABLE_NAME`;")
        database.execSQL("DROP TABLE IF EXISTS `$SOURCE_TABLE_NAME`;")
        database.execSQL("ALTER TABLE source_temp RENAME TO `$SOURCE_TABLE_NAME`;")

        // add source_id
        database.execSQL("CREATE TABLE IF NOT EXISTS `post_temp` (`source_id` INTEGER DEFAULT 0,`bookmarked` INTEGER NOT NULL, `link` TEXT NOT NULL, `pub_date` TEXT NOT NULL, `pub_date_timestamp` INTEGER NOT NULL, `title` TEXT NOT NULL, `author` TEXT NOT NULL, `content_raw` TEXT NOT NULL, `channel_title` TEXT NOT NULL, `channel_image_url` TEXT NOT NULL, `channel_link` TEXT NOT NULL, `content_image` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`link`), FOREIGN KEY (source_id) REFERENCES `$SOURCE_TABLE_NAME`(id) ON DELETE CASCADE);")
        database.execSQL("INSERT INTO post_temp (bookmarked, link, pub_date, pub_date_timestamp, title, author, content_raw, channel_title, channel_image_url, channel_link, content_image, content) SELECT bookmarked, link, pub_date, pub_date_timestamp ,title, author, content_raw, channel_title, channel_image_url, channel_link, content_image, content FROM ${AppDatabase.POST_TABLE_NAME};")
        database.execSQL("DROP TABLE IF EXISTS `$POST_TABLE_NAME`;")
        database.execSQL("ALTER TABLE post_temp RENAME TO `$POST_TABLE_NAME`;")

        // bind posts to sources
        database.execSQL("UPDATE $POST_TABLE_NAME SET source_id = (SELECT id FROM $SOURCE_TABLE_NAME WHERE url = $POST_TABLE_NAME.channel_link)")
    }
}