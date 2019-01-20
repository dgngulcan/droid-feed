package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.adapter.diff.UIModelDiffCallback
import kotlinx.coroutines.*

/**
 * Generic [RecyclerView.Adapter] for [BaseUIModel]s.
 */
@Suppress("UNCHECKED_CAST")
class UIModelAdapter constructor(
    coroutineScope: CoroutineScope,
    val layoutManager: RecyclerView.LayoutManager? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CoroutineScope by coroutineScope {

    private val uiModels = mutableListOf<BaseUIModelAlias>()
    private val viewTypes = SparseArrayCompat<BaseUIModelAlias>()

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
        when (position) {
            in 0 until itemCount -> uiModels[position].getViewType()
            else -> 0
        }


    @Synchronized
    fun addUIModels(newUiModels: List<BaseUIModelAlias>?) {
        newUiModels?.let { newModels ->
            if (uiModels.isEmpty()) {
                uiModels.addAll(newModels)
                updateViewTypes(newModels)
                notifyDataSetChanged()
            } else {
                launch(Dispatchers.IO) {
                    val oldItems = async { ArrayList(uiModels) }

                    val diffResult = async {
                        DiffUtil.calculateDiff(
                            UIModelDiffCallback(
                                oldItems.await(),
                                uiModels as List<BaseUIModelAlias>
                            )
                        )
                    }

                    diffResult.await().let { result ->
                        withContext(Dispatchers.Main) {
                            dispatchUpdates(result)
                        }

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

    private fun updateViewTypes(uiModels: List<BaseUIModelAlias>) {
        uiModels.forEach { baseUiModel ->
            viewTypes.put(baseUiModel.getViewType(), baseUiModel)
        }
    }
}
