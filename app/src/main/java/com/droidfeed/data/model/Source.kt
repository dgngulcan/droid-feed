package com.droidfeed.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.ui.adapter.diff.Diffable

/**
 * Created by Dogan Gulcan on 1/16/18.
 */
@Entity(tableName = AppDatabase.SOURCE_TABLE_NAME)
data class Source(
        @PrimaryKey
        @ColumnInfo(name = "url")
        val url: String,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "is_active")
        var isActive: Boolean
) : Diffable {
    override fun isSame(item: Diffable): Boolean = (item as Source).url.contentEquals(url)

    override fun isContentSame(item: Diffable): Boolean {
        return false
    }
}