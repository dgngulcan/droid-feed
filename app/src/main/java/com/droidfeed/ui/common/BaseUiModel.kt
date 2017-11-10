package com.droidfeed.ui.common

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.adapter.diff.Diffable

/**
 * Base UI model to be used with [UiModelAdapter]
 *
 * Created by Dogan Gulcan on 11/2/17.
 */
abstract class BaseUiModel<T : RecyclerView.ViewHolder> {

    abstract fun getViewHolder(parent: ViewGroup): T

    abstract fun bindViewHolder(viewHolder: T)

    abstract fun getViewType(): Int

    abstract fun getData(): Diffable

}