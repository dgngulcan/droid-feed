package com.droidfeed.data.model

import android.arch.persistence.room.ColumnInfo
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Channel(
    @ColumnInfo(name = "channel_title")
    var title: String = "",

    @ColumnInfo(name = "channel_image_url")
    var imageUrl: String = "",

    @ColumnInfo(name = "channel_link")
    var link: String = ""
) : Parcelable