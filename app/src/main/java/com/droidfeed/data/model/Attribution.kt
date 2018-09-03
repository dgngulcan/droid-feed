package com.droidfeed.data.model

import com.droidfeed.ui.adapter.diff.Diffable

data class Attribution(
    var name: String,
    var description: String,
    var urls: List<String>
) : Diffable {

    override fun isSame(item: Diffable): Boolean {
        return name.contentEquals((item as Attribution).name)
    }

    override fun isContentSame(item: Diffable): Boolean {
        return name.contentEquals((item as Attribution).name)
    }
}