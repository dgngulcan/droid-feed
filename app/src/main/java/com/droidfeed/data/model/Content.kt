package com.droidfeed.data.model

import android.arch.persistence.room.ColumnInfo
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Content(
    @ColumnInfo(name = "content_image")
    var contentImage: String = "",

    @ColumnInfo(name = "content")
    var content: String = ""
) : Parcelable