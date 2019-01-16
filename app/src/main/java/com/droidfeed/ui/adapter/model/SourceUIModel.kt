package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Source
import com.droidfeed.databinding.ListItemSourceBinding
import com.droidfeed.ui.adapter.BaseUIModel
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.viewholder.SourceViewHolder

/**
 * UI model for feed sources.
 */
class SourceUIModel(
    private val source: Source,
    private val clickListener: UiModelClickListener<Source>
) : BaseUIModel<SourceViewHolder> {

    override fun getViewHolder(parent: ViewGroup): SourceViewHolder {
        return SourceViewHolder(
            ListItemSourceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(viewHolder: SourceViewHolder) {
        viewHolder.bind(source, clickListener)
    }

    override fun getViewType(): Int = UIModelType.SOURCE.ordinal

    override fun getData() = source
}