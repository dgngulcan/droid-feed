package com.droidfeed.data.model

import android.arch.persistence.room.*
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.ui.adapter.diff.Diffable

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Entity(tableName = AppDatabase.RSS_TABLE_NAME)
data class Article(
        @PrimaryKey
        @ColumnInfo(name = "link")
        var link: String = "",

        @ColumnInfo(name = "pub_date")
        var pubDate: String = "",

        @Embedded
        var channel: Channel = Channel(),

        @ColumnInfo(name = "title")
        var title: String = "",

        @ColumnInfo(name = "author")
        var author: String = "",

        @ColumnInfo(name = "description")
        var description: String = "",

        @ColumnInfo(name = "image")
        var image: String = "",

        @ColumnInfo(name = "content")
        var content: String = "",

        @Ignore
        var hasFadedIn: Boolean = false

) : Diffable {

    override fun isSame(item: Diffable): Boolean {
        return link.contentEquals((item as Article).link)
    }

    override fun isContentSame(item: Diffable): Boolean {
        return this.link.contentEquals((item as Article).link)
    }

}