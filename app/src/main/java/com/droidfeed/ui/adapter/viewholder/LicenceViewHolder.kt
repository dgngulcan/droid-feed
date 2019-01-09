package com.droidfeed.ui.adapter.viewholder

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.R
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.ListItemLicenceBinding
import com.droidfeed.util.extention.getClickableSpan

class LicenceViewHolder(
    private val binding: ListItemLicenceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(licence: Licence, listener: (String) -> Unit) {
        binding.licence = licence
        binding.container.setOnClickListener {
            listener.invoke(licence.url)
        }

        val text = binding.root.context.getString(R.string.licence)
        val span = text.getClickableSpan(
            text,
            ContextCompat.getColor(binding.root.context, android.R.color.white)
        ) {
            listener.invoke(licence.licenceUrl)
        }

//        binding.licenceSpan = span
        binding.spnLicence.movementMethod = LinkMovementMethod.getInstance()
    }
}