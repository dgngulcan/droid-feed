package com.droidfeed.ui.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.droidfeed.ui.adapter.BaseUIModelAlias

/**
 * [DiffUtil.Callback] for UI models.
 */
class UIModelDiffCallback(
    private val oldModels: List<BaseUIModelAlias>,
    private val newModels: List<BaseUIModelAlias>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) =
        if (oldModels[oldItemPosition].javaClass != newModels[newItemPosition].javaClass) {
            false
        } else {
            oldModels[oldItemPosition].getData().isSame(newModels[newItemPosition].getData())
        }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ) = oldModels[oldItemPosition]
        .getData()
        .hasSameContentWith(newModels[newItemPosition].getData())

    override fun getOldListSize(): Int = oldModels.size

    override fun getNewListSize(): Int = newModels.size

    override fun getChangePayload(
        oldItemPosition: Int,
        newItemPosition: Int
    ) =
        newModels[newItemPosition]
            .getData()
}