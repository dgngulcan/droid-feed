package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.util.logThrowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 */
class UiModelPaginatedAdapter : PagedListAdapter<BaseUiModelAlias, RecyclerView.ViewHolder>(
    BaseUiModelDiffCallback()
) {

    private val viewTypes = SparseArrayCompat<BaseUiModelAlias>()

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

    override fun submitList(pagedList: PagedList<BaseUiModelAlias>?) {
        GlobalScope.launch(Dispatchers.IO) {
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

    override fun getItemViewType(position: Int): Int {
        return try {
            when {
                position in 0 until itemCount && itemCount > 0 -> currentList?.let { pagedList ->
                    pagedList[position]?.getViewType()
                } ?: 0
                else -> 0
            }
        } catch (e: IndexOutOfBoundsException) {
            logThrowable(e)
            0
        }
    }
}

typealias BaseUiModelAlias = BaseUiModel<in RecyclerView.ViewHolder>