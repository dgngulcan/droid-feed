package com.droidfeed.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding
import com.droidfeed.ui.adapter.UiModelClickListener

class LicenceViewHolder(
    private val binding: ListItemLicenceBinding
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(licence: Licence, licenceClickListener: UiModelClickListener<Licence>) {
        binding.licence = licence
        binding.licenceClickListener = licenceClickListener
    }
}