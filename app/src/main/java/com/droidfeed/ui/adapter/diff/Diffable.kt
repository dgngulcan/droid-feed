package com.droidfeed.ui.adapter.diff

/**
 *
 * Created by Dogan Gulcan on 11/8/17.
 */
interface Diffable {

    fun isSame(item: Diffable): Boolean

    fun isContentSame(item: Diffable): Boolean

}