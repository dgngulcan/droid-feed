package com.droidfeed.data.model

import android.arch.persistence.room.*
import android.content.Intent
import android.databinding.ObservableInt
import android.os.Parcelable
import com.droidfeed.R
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Parcelize
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
    var layoutType: UiModelType = UiModelType.ARTICLE_SMALL

) : Diffable, Comparable<Article>, Parcelable {

    @Transient
    @ColumnInfo(name = "bookmarked")
    var bookmarked: Int = 0
        set(value) {
            field = value

            if (value == 1) {
                bookmarkObservable.set(R.drawable.avd_bookmark_positive)
            } else {
                bookmarkObservable.set(R.drawable.avd_bookmark_negative)
            }
        }

    @Transient
    @Ignore
    val bookmarkObservable = ObservableInt(R.drawable.avd_bookmark_negative)

    @Transient
    @ColumnInfo(name = "contentImage")
    var image: String = ""
        get() = if (content.contentImage.isBlank()) channel.imageUrl else content.contentImage

    fun getShareIntent(): Intent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "$title\n\n$link")
        sendIntent.type = "text/plain"
        return sendIntent
    }

    override fun compareTo(other: Article): Int =
        compareValuesBy(this, other) { it.pubDateTimestamp }

    override fun isSame(item: Diffable): Boolean = this.link.contentEquals((item as Article).link)

    override fun isContentSame(item: Diffable): Boolean {
        item as Article

        val content1 = this.bookmarked == item.bookmarked
        val content2 = this.link.contentEquals(item.link)

        return content1 && content2
    }
}