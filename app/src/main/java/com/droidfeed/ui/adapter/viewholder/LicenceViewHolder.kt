package com.droidfeed.ui.adapter.viewholder

import android.databinding.generated.callback.OnClickListener
import android.support.v7.widget.RecyclerView
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding

/**
 * Created by Dogan Gulcan on 11/2/17.
 */
class LicenceViewHolder(private val binding: ListItemLicenceBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(licence: Licence, onClickListener: OnClickListener) {
        binding.licence = licence
        binding.onClickListener = onClickListener

    }

}