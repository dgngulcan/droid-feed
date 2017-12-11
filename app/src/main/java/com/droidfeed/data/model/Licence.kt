package com.droidfeed.data.model

import com.droidfeed.ui.adapter.diff.Diffable

/**
 * Created by Dogan Gulcan on 11/5/17.
 */
data class Licence(var name: String,
                   var description: String,
                   var url: String
) : Diffable {

    override fun isSame(item: Diffable): Boolean {
        return url.contentEquals((item as Licence).url)
    }

    override fun isContentSame(item: Diffable): Boolean {
        return url.contentEquals((item as Licence).url)
    }
}