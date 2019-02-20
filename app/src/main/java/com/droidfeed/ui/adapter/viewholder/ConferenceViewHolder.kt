package com.droidfeed.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.R
import com.droidfeed.data.model.Conference
import com.droidfeed.databinding.ListItemConferenceBinding
import java.util.*


class ConferenceViewHolder(
    private val binding: ListItemConferenceBinding
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("NewApi")
    fun bind(
        conference: Conference,
        onItemClick: (Conference) -> Unit,
        onCFPClick: (Conference) -> Unit
    ) {
        binding.conference = conference

        if (conference.cfpEndDate != null) {
            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
            val formatString = dateFormat.format(conference.cfpEndDate)

            binding.cfpButtonText = binding.root.context.getString(
                R.string.cfp_deadline,
                formatString
            )
        }

        binding.itemCard.setOnClickListener {
            onItemClick(conference)
        }

        binding.btnCFP.setOnClickListener {
            onCFPClick(conference)
        }
    }

}