package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.adapter.diff.UiModelDiffCallback
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.util.logStackTrace
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 */
@Suppress("UNCHECKED_CAST")
class UiModelAdapter constructor(
    private val dataInsertedCallback: DataInsertedCallback? = null,
    val layoutManager: RecyclerView.LayoutManager? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val uiModels = ArrayList<BaseUiModelAlias>()
    private val viewTypes = androidx.collection.SparseArrayCompat<BaseUiModelAlias>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        viewTypes.get(viewType)?.getViewHolder(parent) as RecyclerView.ViewHolder

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        uiModels[position].bindViewHolder(holder)
    }

    override fun getItemCount(): Int = uiModels.size

    override fun getItemViewType(position: Int): Int =
        try {
            when {
                position in 0..(itemCount - 1) && itemCount > 0 -> uiModels[position].getViewType()
                else -> 0
            }
        } catch (e: IndexOutOfBoundsException) {
            logStackTrace(e)
            0
        }

    @Synchronized
    fun addUiModels(newUiModels: Collection<BaseUiModelAlias>?) {
        newUiModels?.let { _ ->
            if (uiModels.isEmpty()) {
                uiModels.addAll(newUiModels)
                updateViewTypes(newUiModels as ArrayList<BaseUiModelAlias>)
                notifyDataSetChanged()

            } else {
                launch(UI) {
                    val oldItems = async { ArrayList(uiModels) }

                    val diffResult = async {
                        DiffUtil.calculateDiff(
                            UiModelDiffCallback(
                                oldItems.await(),
                                uiModels as List<BaseUiModelAlias>
                            )
                        )
                    }

                    diffResult.await().let {
                        dispatchUpdates(it)

                        uiModels.clear()
                        uiModels.addAll(newUiModels)
                        updateViewTypes(uiModels)

                        dataInsertedCallback?.onUpdated()
                    }
                }
            }
        }
    }

    private fun dispatchUpdates(it: DiffUtil.DiffResult) {
        val recyclerViewState = layoutManager?.onSaveInstanceState()
        it.dispatchUpdatesTo(this)
        layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun updateViewTypes(uiModels: ArrayList<BaseUiModelAlias>) {
        uiModels.forEach {
            viewTypes.put(it.getViewType(), it)
        }
    }
}

typealias BaseUiModelAlias = BaseUiModel<in RecyclerView.ViewHolder>