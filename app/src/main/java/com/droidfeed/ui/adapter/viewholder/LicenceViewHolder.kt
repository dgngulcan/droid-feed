package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding

class LicenceViewHolder(
    private val binding: ListItemLicenceBinding
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    fun bind(licence: Licence, listener: () -> Unit) {
        binding.licence = licence
        binding.container.setOnClickListener {
            listener.invoke()
        }
    }
}