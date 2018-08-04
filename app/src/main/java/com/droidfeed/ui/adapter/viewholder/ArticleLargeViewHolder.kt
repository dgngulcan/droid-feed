package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Article
import com.droidfeed.databinding.ListItemArticleLargeBinding
import com.droidfeed.ui.module.feed.ArticleClickListener

class ArticleLargeViewHolder(val binding: ListItemArticleLargeBinding) : ArticleViewHolder(binding.root) {

    override fun bind(article: Article, articleClickListener: ArticleClickListener) {
        binding.articleClickListener = articleClickListener
        binding.rssItem = article
        binding.executePendingBindings()

        bindImage(binding.imgArticle, article)
    }
}