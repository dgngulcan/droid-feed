package com.droidfeed.data.model

import android.content.Intent
import androidx.databinding.ObservableInt
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.droidfeed.R
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable

@Entity(
    tableName = AppDatabase.POST_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = Source::class,
        parentColumns = ["id"],
        childColumns = ["source_id"],
        onDelete = CASCADE
    )]
)
data class Post(
    @PrimaryKey
    @ColumnInfo(name = "link")
    var link: String = "",

    @ColumnInfo(name = "source_id")
    var sourceId: Int? = null,

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
    var layoutType: UiModelType = UiModelType.POST_SMALL

) : Diffable, Comparable<Post> {

    @Transient
    @ColumnInfo(name = "bookmarked")
    var bookmarked: Int = 0
        set(value) {
            field = value

            if (value == 1) {
                bookmarkObservable.set(R.drawable.ic_bookmark_accent_24dp)
            } else {
                bookmarkObservable.set(R.drawable.ic_bookmark_border_accent_24dp)
            }
        }


    @Transient
    @Ignore
    var isVideoContent: Boolean = false
        get() {
            return channel.link.contains("youtube")
        }


    @Transient
    @Ignore
    val bookmarkObservable = ObservableInt(R.drawable.ic_bookmark_border_accent_24dp)

    @Transient
    @Ignore
    var image: String = ""
        get() = if (content.contentImage.isBlank()) channel.imageUrl else content.contentImage

    fun getShareIntent(): Intent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "$title\n\n$link")
        sendIntent.type = "text/plain"
        return sendIntent
    }

    override fun compareTo(other: Post): Int =
        compareValuesBy(this, other) { it.pubDateTimestamp }

    override fun isSame(item: Diffable): Boolean = this.link.contentEquals((item as Post).link)

    override fun isContentSame(item: Diffable): Boolean {
        return if (item is Post) {
            val content1 = this.bookmarked == item.bookmarked
            val content2 = this.link.contentEquals(item.link)

            content1 && content2
        } else {
            false
        }
    }
}