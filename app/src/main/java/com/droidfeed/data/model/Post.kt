package com.droidfeed.data.model

import android.content.Intent
import androidx.databinding.ObservableInt
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.droidfeed.R
import com.droidfeed.data.db.AppDatabase
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.diff.Diffable

@Entity(
    tableName = AppDatabase.POST_TABLE,
    foreignKeys = [ForeignKey(
        entity = Source::class,
        parentColumns = ["id"],
        childColumns = ["source_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["source_id"])]
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
    var layoutType: UIModelType = UIModelType.POST_SMALL

) : Diffable,
    Comparable<Post> {

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
        private set
        get() {
            return channel.link.contains("youtube")
        }

    @Transient
    @Ignore
    val bookmarkObservable = ObservableInt(R.drawable.ic_bookmark_border_accent_24dp)

    @Transient
    @Ignore
    var image: String = ""
        private set
        get() = if (content.contentImage.isBlank()) channel.imageUrl else content.contentImage

    fun isBookmarked() = bookmarked == 1

    fun getShareIntent() = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$link")
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    override fun compareTo(other: Post) = compareValuesBy(this, other) { it.pubDateTimestamp }

    override fun isSame(item: Any) = link == (item as Post).link

}