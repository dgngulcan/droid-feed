package com.droidfeed.ui.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding

/**
 * Created by Dogan Gulcan on 11/2/17.
 */
class LicenceViewHolder(private val binding: ListItemLicenceBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(licence: Licence, onClickListener: View.OnClickListener) {
        binding.licence = licence
        binding.onClickListener = onClickListener

    }

}