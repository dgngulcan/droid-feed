package com.droidfeed.data.model

import com.droidfeed.ui.adapter.diff.Diffable

data class License(
    val name: String,
    val description: String,
    val url: String,
    val licenseUrl: String = ""
) : Diffable {

    override fun isSame(item: Any) = equals(item)

}