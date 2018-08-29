package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemPostSmallBinding
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * [ArticleViewHolder] for the small article cards.
 *
 * @param binding
 */
class ArticleSmallViewHolder(private val binding: ListItemPostSmallBinding) : ArticleViewHolder(binding.root) {

    override fun bind(post: Post, articleClickListener: ArticleClickListener) {
        binding.articleClickListener = articleClickListener
        binding.rssItem = post
        binding.executePendingBindings()

        bindImage(binding.imgPost, post)
    }
}