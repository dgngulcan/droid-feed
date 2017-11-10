package com.droidfeed.ui.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.droidfeed.ui.adapter.diff.UiModelDiffCallback
import com.droidfeed.ui.common.BaseUiModel
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 *
 * Created by Dogan Gulcan on 11/2/17.
 */
class UiModelAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val uiModels = ArrayList<BaseUiModel<RecyclerView.ViewHolder>>()
    private val viewTypes = SparseArrayCompat<BaseUiModel<in RecyclerView.ViewHolder>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewTypes.get(viewType).getViewHolder(parent) as RecyclerView.ViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        uiModels[position].bindViewHolder(holder)
    }

    override fun getItemCount(): Int {
        return uiModels.size
    }

    override fun getItemViewType(position: Int): Int {
        return uiModels[position].getViewType()
    }

    fun addUiModels(uiModels: Collection<BaseUiModel<out RecyclerView.ViewHolder>>?) {
        if (uiModels != null && uiModels.isNotEmpty()) {

            val diffResult = getDiffResult(uiModels)

            this.uiModels.clear()
            this.uiModels.addAll(uiModels as Collection<BaseUiModel<in RecyclerView.ViewHolder>>)

            updateViewTypes(this.uiModels)

            diffResult.dispatchUpdatesTo(this)

        }
    }

    private fun getDiffResult(uiModels: Collection<BaseUiModel<out RecyclerView.ViewHolder>>): DiffUtil.DiffResult {
        val diffCallback = UiModelDiffCallback(this.uiModels,
                uiModels as List<BaseUiModel<out RecyclerView.ViewHolder>>)

        return DiffUtil.calculateDiff(diffCallback)
    }

    private fun updateViewTypes(uiModels: ArrayList<BaseUiModel<RecyclerView.ViewHolder>>) {
        uiModels.forEach {
            viewTypes.put(it.getViewType(), it)
        }
    }


}