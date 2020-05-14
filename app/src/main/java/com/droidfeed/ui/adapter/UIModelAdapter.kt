package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.adapter.diff.UIModelDiffCallback
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Generic [RecyclerView.Adapter] for [BaseUIModel]s.
 */
@Suppress("UNCHECKED_CAST")
class UIModelAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var layoutManager: RecyclerView.LayoutManager? = null
    private val uiModels = mutableListOf<BaseUIModelAlias>()
    private val viewTypes = SparseArrayCompat<BaseUIModelAlias>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return viewTypes.get(viewType)?.getViewHolder(parent) as RecyclerView.ViewHolder
    }

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
                GlobalScope.launch {
                    val diffResult = async {
                        val diffCallback = UIModelDiffCallback(
                            ArrayList(uiModels),
                            newModels
                        )

                        uiModels.clear()
                        uiModels.addAll(newModels)

                        DiffUtil.calculateDiff(diffCallback, true)
                    }

                    withContext(Dispatchers.Main) {
                        dispatchUpdates(diffResult.await())
                    }

                    updateViewTypes(uiModels)
                }
            }
        }
    }

    private fun dispatchUpdates(diffResult: DiffUtil.DiffResult) {
        val recyclerViewState = layoutManager?.onSaveInstanceState()
        diffResult.dispatchUpdatesTo(this)
        layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun updateViewTypes(uiModels: List<BaseUIModelAlias>) {
        uiModels.forEach { baseUiModel ->
            viewTypes.put(baseUiModel.getViewType(), baseUiModel)
        }
    }

}
