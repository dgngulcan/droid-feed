package com.droidfeed.data.model

import androidx.room.ColumnInfo

data class Channel(
    @ColumnInfo(name = "channel_title")
    var title: String = "",

    @ColumnInfo(name = "channel_image_url")
    var imageUrl: String = "",

    @ColumnInfo(name = "channel_link")
    var link: String = ""

)