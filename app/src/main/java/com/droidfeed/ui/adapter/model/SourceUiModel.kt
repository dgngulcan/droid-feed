package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Source
import com.droidfeed.databinding.ListItemSourceBinding
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.SourceViewHolder
import com.droidfeed.ui.common.BaseUiModel

/**
 * UI model for feed sources.
 *
 * Created by Dogan Gulcan on 11/9/17.
 */
class SourceUiModel(
    private val source: Source,
    private val sourceClickListener: UiModelClickListener<Source>

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
        viewHolder.bind(source, sourceClickListener)
    }

    override fun getViewType(): Int = UiModelType.SOURCE.ordinal

    override fun getData(): Diffable = source

}