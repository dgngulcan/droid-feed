package com.droidfeed.data.model

import com.droidfeed.ui.adapter.diff.Diffable

data class Licence(
    val name: String,
    val description: String,
    val url: String,
    val licenceUrl: String = ""
) : Diffable {

    override fun isSame(item: Any) = equals(item)

    override fun hasSameContentWith(item: Any) = equals(item)
}