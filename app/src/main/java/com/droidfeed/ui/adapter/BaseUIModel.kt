package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.adapter.diff.Diffable

/**
 * Base UI model to be used with [UIModelAdapter].
 */
interface BaseUIModel<T : RecyclerView.ViewHolder> {

    fun getViewHolder(parent: ViewGroup): T

    fun bindViewHolder(viewHolder: T)

    fun getViewType(): Int

    fun getData(): Diffable
}