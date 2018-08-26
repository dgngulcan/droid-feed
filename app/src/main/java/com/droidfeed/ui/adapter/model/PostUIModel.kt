package com.droidfeed.ui.adapter.model

import android.view.LayoutInflater
import android.view.ViewGroup
import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemArticleLargeBinding
import com.droidfeed.databinding.ListItemArticleSmallBinding
import com.droidfeed.ui.adapter.UiModelType
import com.droidfeed.ui.adapter.diff.Diffable
import com.droidfeed.ui.adapter.viewholder.ArticleLargeViewHolder
import com.droidfeed.ui.adapter.viewholder.ArticleSmallViewHolder
import com.droidfeed.ui.adapter.viewholder.ArticleViewHolder
import com.droidfeed.ui.common.BaseUiModel
import com.droidfeed.ui.module.feed.ArticleClickListener

data class PostUIModel(
    private val post: Post,
    private val onRssClickListener: ArticleClickListener
) : BaseUiModel<ArticleViewHolder>() {

    override fun getViewHolder(parent: ViewGroup): ArticleViewHolder =
        when (post.layoutType) {
            UiModelType.POST_SMALL -> ArticleSmallViewHolder(
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

    override fun bindViewHolder(viewHolder: ArticleViewHolder) {
        viewHolder.bind(post, onRssClickListener)
    }

    override fun getViewType(): Int = post.layoutType.ordinal

    override fun getData(): Diffable = post
}