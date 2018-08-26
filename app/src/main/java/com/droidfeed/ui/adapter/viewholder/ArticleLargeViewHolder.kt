package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemArticleLargeBinding
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * [ArticleViewHolder] for the large article cards.
 */
class ArticleLargeViewHolder(val binding: ListItemArticleLargeBinding) : ArticleViewHolder(binding.root) {

    override fun bind(article: Post, articleClickListener: ArticleClickListener) {
        binding.articleClickListener = articleClickListener
        binding.rssItem = article
        binding.executePendingBindings()

        bindImage(binding.imgArticle, article)
    }
}