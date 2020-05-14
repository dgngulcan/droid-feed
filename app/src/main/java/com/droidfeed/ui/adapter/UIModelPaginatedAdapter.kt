package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.adapter.diff.BaseUIModelDiffCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Generic [RecyclerView.Adapter] for [BaseUIModel]s.
 */
class UIModelPaginatedAdapter @Inject constructor() : PagedListAdapter<BaseUIModelAlias,
        RecyclerView.ViewHolder>(BaseUIModelDiffCallback()) {

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
        GlobalScope.launch {
            pagedList?.forEach { uiModel -> viewTypes.put(uiModel.getViewType(), uiModel) }

            withContext(Dispatchers.Main) {
                super.submitList(pagedList)
            }
        }
    }

    override fun getItemViewType(position: Int) =
        when (position) {
            in 0 until itemCount -> currentList?.get(position)?.getViewType() ?: 0
            else -> 0
        }
}

typealias BaseUIModelAlias = BaseUIModel<in RecyclerView.ViewHolder>