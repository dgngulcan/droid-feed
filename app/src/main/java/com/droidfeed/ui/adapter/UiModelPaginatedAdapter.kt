package com.droidfeed.ui.adapter

import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
import android.support.annotation.LayoutRes
import android.support.v4.util.SparseArrayCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.databinding.ListItemPlaceholderPostBinding
import com.droidfeed.ui.adapter.viewholder.PostDummyViewHolder
import com.droidfeed.ui.common.BaseUiModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 */
@Suppress("UNCHECKED_CAST")
class UiModelPaginatedAdapter(
    @LayoutRes private val placeHolderResId: Int
) : PagedListAdapter<BaseUiModelAlias, RecyclerView.ViewHolder>(diffCallback) {

    private val viewTypes = SparseArrayCompat<BaseUiModelAlias>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val uiModelType = UiModelType.values()[viewType]

        return when (uiModelType) {
            UiModelType.PLACEHOLDER -> { // TODO find an elegant way
                PostDummyViewHolder(
                    ListItemPlaceholderPostBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> viewTypes.get(viewType).getViewHolder(parent) as RecyclerView.ViewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
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
        return if (currentList != null && currentList!!.isNotEmpty()) {
            currentList?.let { it[position]?.getViewType() } ?: 0
        } else {
            UiModelType.PLACEHOLDER.ordinal
        }
    }

    companion object {
        val diffCallback = object :
            DiffUtil.ItemCallback<BaseUiModelAlias>() {

            override fun areItemsTheSame(
                oldItem: BaseUiModelAlias,
                newItem: BaseUiModelAlias
            ): Boolean {
                return if (oldItem.javaClass != newItem.javaClass) {
                    false
                } else {
                    oldItem.getData().isSame(newItem.getData())
                }
            }

            override fun areContentsTheSame(
                oldConcert: BaseUiModelAlias,
                newConcert: BaseUiModelAlias
            ): Boolean =
                oldConcert == newConcert

            override fun getChangePayload(oldItem: BaseUiModelAlias, newItem: BaseUiModelAlias): Any? {
                return newItem.getData()
            }
        }
    }
}
