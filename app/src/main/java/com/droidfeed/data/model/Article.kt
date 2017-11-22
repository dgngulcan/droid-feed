package com.droidfeed.data.model

import android.arch.persistence.room.*
import android.databinding.ObservableInt
import com.droidfeed.R
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.ui.adapter.UiModelType
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

        @ColumnInfo(name = "title")
        var title: String = "",

        @ColumnInfo(name = "author")
        var author: String = "",

        @ColumnInfo(name = "content_raw")
        var rawContent: String = "",

        @Embedded
        var channel: Channel = Channel(),

        @Embedded
        var content: Content = Content(),

        @Ignore
        var hasFadedIn: Boolean = false,

        @Ignore
        var layoutType: UiModelType = UiModelType.ArticleSmall

) : Diffable, Comparable<Article> {

    @ColumnInfo(name = "bookmarked")
    var bookmarked: Int = 0
        set(value) {
            field = value

            if (value == 1) {
                bookmarkIcon.set(R.drawable.ic_bookmark_black_24dp)
            } else {
                bookmarkIcon.set(R.drawable.ic_bookmark_border_black_24dp)
            }
        }


    @Ignore
    var bookmarkIcon = ObservableInt(R.drawable.ic_bookmark_border_black_24dp)

    @ColumnInfo(name = "contentImage")
    var image: String = ""
        get() = if (content.contentImage.isBlank()) channel.imageUrl else content.contentImage

    override fun compareTo(other: Article): Int =
            compareValuesBy(this, other, { it.pubDateTimestamp })

    override fun isSame(item: Diffable): Boolean =
            link.contentEquals((item as Article).link)

    override fun isContentSame(item: Diffable): Boolean =
            this.link.contentEquals((item as Article).link)

}