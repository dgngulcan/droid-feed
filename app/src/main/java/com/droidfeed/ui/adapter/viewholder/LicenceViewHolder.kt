package com.droidfeed.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.License
import com.droidfeed.databinding.ListItemLicenseBinding

class LicenseViewHolder(
    private val binding: ListItemLicenseBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        license: License,
        urlListener: (String) -> Unit
    ) {
        binding.license = license
        binding.container.setOnClickListener { urlListener(license.url) }
        binding.btnLicense.setOnClickListener { urlListener(license.licenseUrl) }
    }
}