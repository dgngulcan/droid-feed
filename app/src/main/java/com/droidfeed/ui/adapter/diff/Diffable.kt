package com.droidfeed.ui.adapter.diff

/**
 * Should be implemented if the class will be used with the [UIModelDiffCallback].
 */
interface Diffable {

    fun isSame(item: Any): Boolean

    fun hasSameContentWith(item: Any): Boolean
}