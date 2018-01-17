package com.droidfeed.data.model

import com.droidfeed.ui.adapter.diff.Diffable

/**
 * Created by Dogan Gulcan on 1/16/18.
 */
data class Source(val name: String, val url: String): Diffable {
    override fun isSame(item: Diffable): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isContentSame(item: Diffable): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}