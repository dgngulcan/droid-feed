package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Conference
import com.droidfeed.databinding.ListItemConferenceBinding
import com.droidfeed.ui.adapter.BaseUIModel
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.ConferenceViewHolder

class ConferenceUIModel(
    private val conference: Conference,
    private val onItemClick: (Conference) -> Unit,
    private val onCFPClick: (Conference) -> Unit
) : BaseUIModel<ConferenceViewHolder> {

    override fun getViewHolder(parent: ViewGroup): ConferenceViewHolder {
        return ConferenceViewHolder(
            ListItemConferenceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindViewHolder(viewHolder: ConferenceViewHolder) {
        viewHolder.bind(
            conference,
            onItemClick,
            onCFPClick
        )
    }

    override fun getViewType(): Int = UIModelType.LICENCE.ordinal

    override fun getData(): Diffable = conference
}