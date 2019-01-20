package com.droidfeed.data.model

import androidx.databinding.ObservableBoolean
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.ui.adapter.diff.Diffable

@Entity(tableName = AppDatabase.SOURCE_TABLE_NAME)
data class Source(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "url")
    val url: String

) : Diffable {

    @ColumnInfo(name = "is_active")
    var isActive: Boolean = true
        set(value) {
            isEnabled.set(value)
            field = value
        }

    @Ignore
    val isEnabled = ObservableBoolean()

    override fun isSame(item: Any) = url == (item as Source).url

    override fun hasSameContentWith(item: Any) = isActive == (item as Source).isActive
}