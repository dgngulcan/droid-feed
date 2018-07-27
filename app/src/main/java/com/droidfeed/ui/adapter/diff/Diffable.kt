package com.droidfeed.ui.adapter.diff

/**
 * Should be implemented if the class will be used with the [UiModelDiffCallback].
 *
 * Created by Dogan Gulcan on 11/8/17.
 */
interface Diffable {

    fun isSame(item: Diffable): Boolean

    fun isContentSame(item: Diffable): Boolean
}