package com.droidfeed.ui.adapter.diff

import android.support.v7.util.DiffUtil
import com.droidfeed.ui.adapter.BaseUiModelAlias

/**
 * [DiffUtil.Callback] for UI models.
 *
 * Created by Dogan Gulcan on 11/7/17.
 */
class UiModelDiffCallback(
        private val oldModels: List<BaseUiModelAlias>,
        private val newModels: List<BaseUiModelAlias>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (oldModels[oldItemPosition].javaClass != newModels[newItemPosition].javaClass) {
            false
        } else {
            oldModels[oldItemPosition].getData().isSame(newModels[newItemPosition].getData())
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldModels[oldItemPosition].getData().isContentSame(newModels[newItemPosition].getData())

    override fun getOldListSize(): Int = oldModels.size

    override fun getNewListSize(): Int = newModels.size

}