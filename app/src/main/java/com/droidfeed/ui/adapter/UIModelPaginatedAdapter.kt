package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Generic [RecyclerView.Adapter] for [BaseUIModel]s.
 */
class UIModelPaginatedAdapter(
    coroutineScope: CoroutineScope
) : PagedListAdapter<BaseUIModelAlias, RecyclerView.ViewHolder>(BaseUiModelDiffCallback()),
    CoroutineScope by coroutineScope {

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
        val uiModel = getItem(position)
        uiModel?.bindViewHolder(holder)
    }

    override fun submitList(pagedList: PagedList<BaseUIModel<in RecyclerView.ViewHolder>>?) {
        launch(Dispatchers.IO) {
            if (pagedList == null || pagedList.isEmpty()) {
                super.submitList(pagedList)
            } else {
                val parseViewTypes = launch {
                    pagedList.forEach { uiModel ->
                        if (uiModel != null) {
                            viewTypes.put(uiModel.getViewType(), uiModel)
                        }
                    }
                }

                parseViewTypes.join()
                withContext(Dispatchers.Main) { super.submitList(pagedList) }
            }
        }
    }

    fun isEmpty() = itemCount == 0

    override fun getItemViewType(position: Int) =
        when (position) {
            in 0 until itemCount -> currentList?.let { pagedList ->
                pagedList[position]?.getViewType()
            } ?: 0
            else -> 0
        }
}

typealias BaseUIModelAlias = BaseUIModel<in RecyclerView.ViewHolder>