package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Article
import com.droidfeed.databinding.ListItemNewsBinding
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.viewholder.RssViewHolder
import com.droidfeed.ui.binding.DateDataBindingComponent
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.ui.module.news.NewsItemClickListener

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
data class RssListUiModel(
        val rssItem: Article,
        val onRssClickListener: NewsItemClickListener
) : BaseUiModel<RssViewHolder>() {

    val dataBindingComponent: DateDataBindingComponent = DateDataBindingComponent()

    override fun getViewHolder(parent: ViewGroup): RssViewHolder {
        return RssViewHolder(ListItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
                dataBindingComponent))
    }

    override fun bindViewHolder(viewHolder: RssViewHolder) {
        viewHolder.bind(rssItem, onRssClickListener)
    }

    override fun getViewType(): Int {
        return UiModelType.RssFeed.ordinal
    }
}