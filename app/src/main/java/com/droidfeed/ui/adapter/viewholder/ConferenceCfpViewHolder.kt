package com.droidfeed.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.DateFormat
import com.droidfeed.R
import com.droidfeed.data.model.Conference
import com.droidfeed.databinding.ListItemConferenceBinding
import java.util.*

class ConferenceCfpViewHolder(
    private val binding: ListItemConferenceBinding
) : ConferenceViewHolder(binding) {

    private var cfpText = ""

    fun bindCfp(
        conference: Conference,
        onCFPClick: (Conference) -> Unit
    ) {
        if (conference.isCfpOpen() && cfpText.isBlank()) {
            cfpText = getFormattedDate(
                binding.root.context,
                conference
            )
        }

        binding.apply {
            this.cfpButtonText = cfpText

            btnCFP.setOnClickListener {
                onCFPClick(conference)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun getFormattedDate(
        context: Context,
        conference: Conference
    ): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return context.getString(
            R.string.cfp_deadline,
            dateFormat.format(conference.cfpEndDate) ?: ""
        )
    }
}