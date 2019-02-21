package com.droidfeed.data.model

import com.droidfeed.ui.adapter.diff.Diffable
import java.util.*

data class Conference(
    val name: String,
    val location: String,
    val url: String,
    val startDate: Date,
    val endDate: Date,
    val cfpStartDate: Date? = null,
    val cfpEndDate: Date? = null,
    val cfpUrl: String? = null
) : Diffable {

    fun isCfpOpen() = cfpEndDate != null && cfpEndDate.after(Date())

    override fun isSame(item: Any) = item == this

    override fun hasSameContentWith(item: Any) = item == this
}