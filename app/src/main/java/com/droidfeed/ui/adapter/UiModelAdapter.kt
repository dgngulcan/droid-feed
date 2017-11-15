package com.droidfeed.ui.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.diff.UiModelDiffCallback
import com.droidfeed.ui.common.BaseUiModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 *
 * Created by Dogan Gulcan on 11/2/17.
 */
typealias BaseUiModelAlias = BaseUiModel<in RecyclerView.ViewHolder, Diffable>

class UiModelAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val uiModels = ArrayList<BaseUiModelAlias>()
    private val viewTypes = SparseArrayCompat<BaseUiModelAlias>()

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

    fun addUiModels(uiModels: Collection<BaseUiModelAlias>?) {
        if (uiModels != null && uiModels.isNotEmpty()) {
            async(UI) {
                val abc = bg {
                    val diffResult = getDiffResult(uiModels)

                    this@UiModelAdapter.uiModels.clear()
                    this@UiModelAdapter.uiModels.addAll(uiModels)

                    updateViewTypes(this@UiModelAdapter.uiModels)
                    diffResult
                }

                abc.await().dispatchUpdatesTo(this@UiModelAdapter)
            }
        }
    }

    private fun getDiffResult(uiModels: Collection<BaseUiModelAlias>): DiffUtil.DiffResult {
        val diffCallback = UiModelDiffCallback(this.uiModels, uiModels as List<BaseUiModelAlias>)

        return DiffUtil.calculateDiff(diffCallback)
    }

    private fun updateViewTypes(uiModels: ArrayList<BaseUiModelAlias>) {
        uiModels.forEach {
            viewTypes.put(it.getViewType(), it)
        }
    }


}