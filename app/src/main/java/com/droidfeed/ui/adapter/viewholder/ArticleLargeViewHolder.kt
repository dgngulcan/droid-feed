package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemPostLargeBinding
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * [ArticleViewHolder] for the large article cards.
 */
class ArticleLargeViewHolder(val binding: ListItemPostLargeBinding) : ArticleViewHolder(binding.root) {

    override fun bind(post: Post, articleClickListener: ArticleClickListener) {
        binding.postClickListener = articleClickListener
        binding.post = post
        binding.executePendingBindings()

        bindImage(binding.imgPost, post)
    }
}