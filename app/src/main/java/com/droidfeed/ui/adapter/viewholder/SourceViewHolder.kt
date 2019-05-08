package com.droidfeed.ui.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.data.model.Source
import com.droidfeed.databinding.ListItemSourceBinding

class SourceViewHolder(
    private val binding: ListItemSourceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        source: Source,
        onItemClick: (Source) -> Unit,
        onRemove: (Source) -> Unit
    ) {
        binding.source = source
        binding.setItemClickListener { onItemClick(source) }
        binding.setRemoveClickListener { onRemove(source) }
    }
}

