package com.droidfeed.ui.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.droidfeed.ui.common.BaseUiModel
import javax.inject.Inject


/**
 * Generic [RecyclerView.Adapter] for [BaseUiModel]s.
 *
 * Created by Dogan Gulcan on 11/2/17.
 */
class UiModelAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val items = ArrayList<BaseUiModel<in RecyclerView.ViewHolder>>()
    private val viewTypes = SparseArrayCompat<BaseUiModel<in RecyclerView.ViewHolder>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewTypes.get(viewType).getViewHolder(parent) as RecyclerView.ViewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items.get(position).bindViewHolder(holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addUiModels(uiModels: Collection<BaseUiModel<out RecyclerView.ViewHolder>>?) {
        if (uiModels != null && uiModels.isNotEmpty()) {
//            DiffUtil.calculateDiff()
            items.addAll(uiModels as Collection<BaseUiModel<in RecyclerView.ViewHolder>>)

            // add types
            uiModels.forEach {
                viewTypes.put(it.getViewType(), it)
            }
            notifyDataSetChanged()
        }
    }


}