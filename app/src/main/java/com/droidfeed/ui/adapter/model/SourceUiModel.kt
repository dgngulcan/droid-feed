package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Source
import com.droidfeed.databinding.ListItemSourceBinding
import com.droidfeed.ui.adapter.BaseUiModel
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.SourceViewHolder

/**
 * UI model for feed sources.
 */
class SourceUiModel(
    private val source: Source,
    private val clickListener: UiModelClickListener<Source>
) : BaseUiModel<SourceViewHolder>() {

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

    override fun getViewType(): Int = UiModelType.SOURCE.ordinal

    override fun getData(): Diffable = source
}