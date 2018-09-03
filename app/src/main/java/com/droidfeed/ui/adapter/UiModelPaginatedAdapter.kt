package com.droidfeed.ui.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.util.logStackTrace
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 */
@Suppress("UNCHECKED_CAST")
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
        launch {
            if (pagedList == null || pagedList.isEmpty()) {
                super.submitList(pagedList)
            } else {
                val parseViewTypes = launch {
                    pagedList.forEach {
                        if (it != null) {
                            viewTypes.put(it.getViewType(), it)
                        }
                    }
                }

                parseViewTypes.join()
                launch(UI) { super.submitList(pagedList) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            when {
                position in 0..(itemCount - 1) && itemCount > 0 -> currentList?.let { it[position]?.getViewType() } ?: 0
                else -> 0
            }
        } catch (e: IndexOutOfBoundsException) {
            logStackTrace(e)
            0
        }
    }

}
