package com.droidfeed.ui.adapter.viewholder

import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.R
import com.droidfeed.data.model.Source
import com.droidfeed.databinding.ListItemSourceBinding


class SourceViewHolder(
    private val binding: ListItemSourceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        source: Source,
        onItemClick: (Source) -> Unit,
        onRemoveClick: (Source) -> Unit,
        onShareClick: (Source) -> Unit
    ) {
        binding.source = source
        binding.setItemClickListener { onItemClick(source) }
        binding.setRemoveClickListener { view ->
            val wrapper = ContextThemeWrapper(itemView.context, R.style.PopupMenuStyle)
            PopupMenu(wrapper, view).apply {
                menuInflater.inflate(R.menu.source_menu, menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_remove -> onRemoveClick(source)
                        R.id.action_share->onShareClick(source)
                    }

                    true
                }
            }.also { it.show() }
        }
    }
}

