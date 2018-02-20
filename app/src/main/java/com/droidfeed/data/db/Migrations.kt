package com.droidfeed.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration
import com.droidfeed.data.db.AppDatabase.Companion.SOURCE_TABLE_NAME


/**
 * Created by Dogan Gulcan on 1/25/18.
 */

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `$SOURCE_TABLE_NAME` (`url` TEXT NOT NULL, `name` TEXT NOT NULL, `is_active` INTEGER NOT NULL, PRIMARY KEY(`url`))")
//        database.close()
    }
}


