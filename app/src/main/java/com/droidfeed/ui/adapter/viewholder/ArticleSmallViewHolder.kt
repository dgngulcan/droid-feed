package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemArticleSmallBinding
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * [ArticleViewHolder] for the small article cards.
 *
 * @param binding
 */
class ArticleSmallViewHolder(private val binding: ListItemArticleSmallBinding) : ArticleViewHolder(binding.root) {

    override fun bind(article: Post, articleClickListener: ArticleClickListener) {
        binding.articleClickListener = articleClickListener
        binding.rssItem = article
        binding.executePendingBindings()

        bindImage(binding.imgArticle, article)
    }
}