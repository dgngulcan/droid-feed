package com.droidfeed.ui.common

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.droidfeed.util.DebugUtils

/**
 * Created by Dogan Gulcan on 12/16/17.
 */
class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            DebugUtils.showStackTrace(e)
        }

    }
}