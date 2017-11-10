package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Article
import com.droidfeed.databinding.ListItemNewsBinding
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.ArticleViewHolder
import com.droidfeed.ui.binding.DateDataBindingComponent
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
data class ArticleUiModel(
        val article: Article,
        val onRssClickListener: ArticleClickListener
) : BaseUiModel<ArticleViewHolder>() {

    val dataBindingComponent: DateDataBindingComponent = DateDataBindingComponent()

    override fun getViewHolder(parent: ViewGroup): ArticleViewHolder {
        return ArticleViewHolder(ListItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
                dataBindingComponent))
    }

    override fun bindViewHolder(viewHolder: ArticleViewHolder) {
        viewHolder.bind(article, onRssClickListener)
    }

    override fun getViewType(): Int {
        return UiModelType.RssFeed.ordinal
    }

    override fun getData(): Diffable {
        return article
    }
}