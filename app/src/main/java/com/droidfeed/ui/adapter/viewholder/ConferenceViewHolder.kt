package com.droidfeed.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.Conference
import com.droidfeed.databinding.ListItemConferenceBinding

open class ConferenceViewHolder(
    private val binding: ListItemConferenceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindConference(
        conference: Conference,
        onItemClick: (Conference) -> Unit
    ) {
        binding.apply {
            this.conference = conference

            itemCard.setOnClickListener {
                onItemClick(conference)
            }

        }
    }
}