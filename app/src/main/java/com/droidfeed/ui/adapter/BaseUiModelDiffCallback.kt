package com.droidfeed.ui.adapter

import androidx.recyclerview.widget.DiffUtil

class BaseUiModelDiffCallback : DiffUtil.ItemCallback<BaseUiModelAlias>() {

    override fun areItemsTheSame(
        oldItem: BaseUiModelAlias,
        newItem: BaseUiModelAlias
    ): Boolean {
        return if (oldItem.javaClass != newItem.javaClass) {
            false
        } else {
            oldItem.getData().isSame(newItem.getData())
        }
    }

    override fun areContentsTheSame(
        oldConcert: BaseUiModelAlias,
        newConcert: BaseUiModelAlias
    ): Boolean =
        oldConcert == newConcert

    override fun getChangePayload(
        oldItem: BaseUiModelAlias,
        newItem: BaseUiModelAlias
    ): Any? {
        return newItem.getData()
    }
}