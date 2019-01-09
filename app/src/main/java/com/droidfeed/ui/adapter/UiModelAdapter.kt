package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.adapter.diff.UiModelDiffCallback
import com.droidfeed.util.logThrowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 */
@Suppress("UNCHECKED_CAST")
class UiModelAdapter constructor(
    val layoutManager: RecyclerView.LayoutManager? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val uiModels = ArrayList<BaseUiModelAlias>()
    private val viewTypes = SparseArrayCompat<BaseUiModelAlias>()

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
                position in 0 until itemCount && itemCount > 0 -> uiModels[position].getViewType()
                else -> 0
            }
        } catch (e: IndexOutOfBoundsException) {
            logThrowable(e)
            0
        }

    @Synchronized
    fun addUiModels(newUiModels: Collection<BaseUiModelAlias>?) {
        newUiModels?.let { newModels ->
            if (uiModels.isEmpty()) {
                uiModels.addAll(newModels)
                updateViewTypes(newModels as ArrayList<BaseUiModelAlias>)
                notifyDataSetChanged()
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    val oldItems = async { ArrayList(uiModels) }

                    val diffResult = async {
                        DiffUtil.calculateDiff(
                            UiModelDiffCallback(
                                oldItems.await(),
                                uiModels as List<BaseUiModelAlias>
                            )
                        )
                    }

                    diffResult.await().let { result ->
                        dispatchUpdates(result)

                        uiModels.clear()
                        uiModels.addAll(newModels)
                        updateViewTypes(uiModels)
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
        uiModels.forEach { baseUiModel ->
            viewTypes.put(baseUiModel.getViewType(), baseUiModel)
        }
    }
}
