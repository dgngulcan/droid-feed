package com.droidfeed.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding

class LicenceViewHolder(
    private val binding: ListItemLicenceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        licence: Licence,
        urlListener: (String) -> Unit
    ) {
        binding.licence = licence
        binding.container.setOnClickListener {
            urlListener(licence.url)
        }
        binding.btnLicence.setOnClickListener {
            urlListener(licence.licenceUrl)
        }
    }
}