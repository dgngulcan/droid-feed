package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.adapter.diff.BaseUIModelDiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Generic [RecyclerView.Adapter] for [BaseUIModel]s.
 */
class UIModelPaginatedAdapter(
    coroutineScope: CoroutineScope
) : PagedListAdapter<BaseUIModelAlias, RecyclerView.ViewHolder>(BaseUIModelDiffCallback()),
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
        launch {
            pagedList?.forEach { uiModel ->
                viewTypes.put(uiModel.getViewType(), uiModel)
            }

            withContext(Dispatchers.Main) {
                super.submitList(pagedList)
            }
        }
    }

    override fun getItemViewType(position: Int) =
        when (position) {
            in 0 until itemCount -> currentList?.let { pagedList ->
                pagedList[position]?.getViewType()
            } ?: 0
            else -> 0
        }
}

typealias BaseUIModelAlias = BaseUIModel<in RecyclerView.ViewHolder>