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

        @ColumnInfo(name = "pub_date_timestamp")
        var pubDateTimestamp: Long = 0,

        @Embedded
        var channel: Channel = Channel(),

        @ColumnInfo(name = "title")
        var title: String = "",

        @ColumnInfo(name = "author")
        var author: String = "",

        @Embedded
        var content: Content = Content(),

        @ColumnInfo(name = "content_raw")
        var rawContent: String = "",

        @Ignore
        var hasFadedIn: Boolean = false

) : Diffable, Comparable<Article> {

    @ColumnInfo(name = "contentImage")
    var image: String = ""
        get() {
            return if (content.contentImage.isBlank()) channel.imageUrl else content.contentImage
        }

    override fun compareTo(other: Article): Int {
        return compareValuesBy(this, other, { it.pubDateTimestamp })
    }

    override fun isSame(item: Diffable): Boolean {
        return link.contentEquals((item as Article).link)
    }

    override fun isContentSame(item: Diffable): Boolean {
        return this.link.contentEquals((item as Article).link)
    }

}