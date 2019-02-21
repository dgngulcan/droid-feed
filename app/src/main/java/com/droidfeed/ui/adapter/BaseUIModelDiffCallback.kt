package com.droidfeed.ui.adapter

import androidx.recyclerview.widget.DiffUtil

class BaseUIModelDiffCallback : DiffUtil.ItemCallback<BaseUIModelAlias>() {

    override fun areItemsTheSame(
        oldItem: BaseUIModelAlias,
        newItem: BaseUIModelAlias
    ) = if (oldItem.javaClass != newItem.javaClass) {
        false
    } else {
        oldItem.getData().isSame(newItem.getData())
    }

    override fun areContentsTheSame(
        oldConcert: BaseUIModelAlias,
        newConcert: BaseUIModelAlias
    ) = oldConcert == newConcert

    override fun getChangePayload(
        oldItem: BaseUIModelAlias,
        newItem: BaseUIModelAlias
    ) = newItem.getData()
}