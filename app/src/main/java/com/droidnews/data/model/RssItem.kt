package com.droidnews.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.droidnews.data.db.AppDatabase
import org.simpleframework.xml.Element

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Entity(tableName = AppDatabase.RSS_TABLE_NAME)
data class RssItem(

        @Element(name = "guid")
        @Embedded(prefix = "guid_")
        val guid: RssGuid,

        @Element(name = "pubDate")
        @ColumnInfo(name = "pub_date")
        val pubDate: String,

        @Element(name = "title")
        @ColumnInfo(name = "title")
        val title: String,

        @Element(name = "content")
        @ColumnInfo(name = "content")
        val content: String,

//        @Element(name = "category")
//        @ColumnInfo(name = "category")
//        val category: Array<String>,

        @PrimaryKey
        @Element(name = "link")
        @ColumnInfo(name = "link")
        val link: String)