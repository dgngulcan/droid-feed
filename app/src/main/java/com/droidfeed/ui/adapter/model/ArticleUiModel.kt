package com.droidfeed.ui.adapter.model

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Article
import com.droidfeed.databinding.ListItemArticleLargeBinding
import com.droidfeed.databinding.ListItemArticleSmallBinding
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.ArticleLargeViewHolder
import com.droidfeed.ui.adapter.viewholder.ArticleSmallViewHolder
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
data class ArticleUiModel(
    private val article: Article,
    private val onRssClickListener: ArticleClickListener
) : BaseUiModel<RecyclerView.ViewHolder>() {


    override fun getViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        when (article.layoutType) {
            UiModelType.ARTICLE_SMALL -> ArticleSmallViewHolder(
                ListItemArticleSmallBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> ArticleLargeViewHolder(
                ListItemArticleLargeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    override fun bindViewHolder(viewHolder: RecyclerView.ViewHolder) {
        when (viewHolder) {
            is ArticleSmallViewHolder -> viewHolder.bind(article, onRssClickListener)
            is ArticleLargeViewHolder -> viewHolder.bind(article, onRssClickListener)
        }
    }

    override fun getViewType(): Int = article.layoutType.ordinal

    override fun getData(): Diffable = article

}