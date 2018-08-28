package com.droidfeed.data.model

import com.droidfeed.ui.adapter.diff.Diffable

data class Licence(
    var name: String,
    var description: String,
    var url: String,
    var licenceUrl: String = ""
) : Diffable {

    override fun isSame(item: Diffable): Boolean {
        return url.contentEquals((item as Licence).url)
    }

    override fun isContentSame(item: Diffable): Boolean {
        return url.contentEquals((item as Licence).url)
    }
}