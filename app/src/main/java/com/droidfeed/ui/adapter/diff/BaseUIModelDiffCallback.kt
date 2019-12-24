package com.droidfeed.ui.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.droidfeed.ui.adapter.BaseUIModelAlias


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
        oldContent: BaseUIModelAlias,
        newContent: BaseUIModelAlias
    ) = oldContent.getData().isSame(newContent.getData())

    override fun getChangePayload(
        oldItem: BaseUIModelAlias,
        newItem: BaseUIModelAlias
    ) = newItem.getData()
}