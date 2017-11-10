package com.droidfeed.ui.adapter.diff

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import com.droidfeed.ui.common.BaseUiModel

/**
 * [DiffUtil.Callback] for UI models.
 *
 * Created by Dogan Gulcan on 11/7/17.
 */
class UiModelDiffCallback(
        private val oldModels: List<BaseUiModel<out RecyclerView.ViewHolder>>,
        private val newModels: List<BaseUiModel<out RecyclerView.ViewHolder>>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return if (oldModels[oldItemPosition].javaClass != newModels[newItemPosition].javaClass) {
            false
        } else {
            oldModels[oldItemPosition].getData().isSame(newModels[newItemPosition].getData())
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldModels[oldItemPosition].getData().isContentSame(newModels[newItemPosition].getData())
    }

    override fun getOldListSize(): Int {
        return oldModels.size
    }

    override fun getNewListSize(): Int {
        return newModels.size
    }

}