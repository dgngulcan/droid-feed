package com.droidfeed.ui.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.util.logStackTrace

class WrapContentLinearLayoutManager(context: Context) : androidx.recyclerview.widget.LinearLayoutManager(context) {

    override fun onLayoutChildren(recycler: androidx.recyclerview.widget.RecyclerView.Recycler?, state: androidx.recyclerview.widget.RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            logStackTrace(e)
        }
    }
}