package com.droidfeed.data.model

import android.arch.persistence.room.ColumnInfo

data class Content(
    @ColumnInfo(name = "content_image")
    var contentImage: String = "",

    @ColumnInfo(name = "content")
    var content: String = ""
)