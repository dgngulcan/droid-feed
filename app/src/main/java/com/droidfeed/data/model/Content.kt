package com.droidfeed.data.model

import android.arch.persistence.room.ColumnInfo

/**
 * Created by Dogan Gulcan on 11/13/17.
 */
data class Content(
        @ColumnInfo(name = "content_image")
        var contentImage: String = "",

        @ColumnInfo(name = "content")
        var content: String = ""
)