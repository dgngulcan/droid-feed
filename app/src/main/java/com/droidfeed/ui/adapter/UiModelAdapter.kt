package com.droidfeed.ui.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.droidfeed.ui.adapter.diff.UiModelDiffCallback
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.util.uiThread
import com.droidfeed.util.workerThread
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 *
 * Created by Dogan Gulcan on 11/2/17.
 */
typealias BaseUiModelAlias = BaseUiModel<in RecyclerView.ViewHolder>

class UiModelAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val uiModels = ArrayList<BaseUiModelAlias>()
    private val viewTypes = SparseArrayCompat<BaseUiModelAlias>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            viewTypes.get(viewType).getViewHolder(parent) as RecyclerView.ViewHolder

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        uiModels[position].bindViewHolder(holder)
    }

    override fun getItemCount(): Int = uiModels.size

    override fun getItemViewType(position: Int): Int {
        return if (position in 0..(itemCount - 1) && itemCount > 0) {
            uiModels[position].getViewType()
        } else {
            0
        }
    }

    @Synchronized
    fun addUiModels(uiModels: Collection<BaseUiModelAlias>?) {
        if (uiModels != null) {
            workerThread {

                val diffResult = DiffUtil.calculateDiff(
                        UiModelDiffCallback(this@UiModelAdapter.uiModels,
                                uiModels as List<BaseUiModelAlias>))

                this@UiModelAdapter.uiModels.clear()
                this@UiModelAdapter.uiModels.addAll(uiModels)

                updateViewTypes(this@UiModelAdapter.uiModels)

                uiThread {
                    diffResult.dispatchUpdatesTo(this@UiModelAdapter)
                }
            }
        }
    }

    private fun updateViewTypes(uiModels: ArrayList<BaseUiModelAlias>) {
        uiModels.forEach {
            viewTypes.put(it.getViewType(), it)
        }
    }

}