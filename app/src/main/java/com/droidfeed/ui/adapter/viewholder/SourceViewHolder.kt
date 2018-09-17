package com.droidfeed.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.Source
import com.droidfeed.databinding.ListItemSourceBinding
import com.droidfeed.ui.adapter.UiModelClickListener

class SourceViewHolder(
    private val binding: ListItemSourceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(source: Source, clickListener: UiModelClickListener<Source>) {
        binding.source = source
        binding.clickListener = clickListener
    }
}