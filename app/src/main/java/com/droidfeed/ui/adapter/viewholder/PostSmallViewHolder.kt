package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemPostSmallBinding
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * [PostViewHolder] for the small article cards.
 *
 * @param binding
 */
class PostSmallViewHolder(private val binding: ListItemPostSmallBinding) : PostViewHolder(binding.root) {

    override fun bind(post: Post, articleClickListener: ArticleClickListener) {
        binding.articleClickListener = articleClickListener
        binding.rssItem = post
        binding.executePendingBindings()

        bindImage(binding.imgPost, post)
    }
}