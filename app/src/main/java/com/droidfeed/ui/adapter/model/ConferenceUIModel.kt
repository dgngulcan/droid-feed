package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.Conference
import com.droidfeed.databinding.ListItemConferenceBinding
import com.droidfeed.ui.adapter.BaseUIModel
import com.droidfeed.ui.adapter.UIModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.ConferenceCfpViewHolder
import com.droidfeed.ui.adapter.viewholder.ConferenceViewHolder

class ConferenceUIModel(
    private val conference: Conference,
    private val onItemClick: (Conference) -> Unit,
    private val onCFPClick: (Conference) -> Unit
) : BaseUIModel<RecyclerView.ViewHolder> {

    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ListItemConferenceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).also { binding ->
            binding.btnCFP.isVisible = conference.isCfpOpen()
        }

        return if (conference.isCfpOpen()) {
            ConferenceCfpViewHolder(binding)
        } else {
            ConferenceViewHolder(binding)
        }
    }

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder) {
        when (viewHolder) {
            is ConferenceCfpViewHolder -> {
                viewHolder.apply {
                    bindConference(
                        conference,
                        onItemClick
                    )
                    bindCfp(
                        conference,
                        onCFPClick
                    )
                }
            }
            is ConferenceViewHolder -> {
                viewHolder.bindConference(
                    conference,
                    onItemClick
                )
            }
        }
    }

    override fun getViewType(): Int = if (conference.isCfpOpen()) {
        UIModelType.CONFERENCE_CFP
    } else {
        UIModelType.CONFERENCE
    }.ordinal

    override fun getData(): Diffable = conference
}
