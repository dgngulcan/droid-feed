package com.droidfeed.ui.adapter.viewholder

import android.support.v7.widget.RecyclerView
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding
import com.droidfeed.ui.adapter.UiModelClickListener

/**
 * Created by Dogan Gulcan on 11/2/17.
 */
class LicenceViewHolder(
    private val binding: ListItemLicenceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(licence: Licence, licenceClickListener: UiModelClickListener<Licence>) {
        binding.licence = licence
        binding.licenceClickListener = licenceClickListener
    }

}