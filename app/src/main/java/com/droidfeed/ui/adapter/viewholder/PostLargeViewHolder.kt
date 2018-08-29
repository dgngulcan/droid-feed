package com.droidfeed.ui.adapter.viewholder

import com.droidfeed.data.model.Post
import com.droidfeed.databinding.ListItemPostLargeBinding
import com.droidfeed.ui.module.feed.ArticleClickListener

/**
 * [PostViewHolder] for the large article cards.
 */
class PostLargeViewHolder(val binding: ListItemPostLargeBinding) : PostViewHolder(binding.root) {

    override fun bind(post: Post, articleClickListener: ArticleClickListener) {
        binding.postClickListener = articleClickListener
        binding.post = post
        binding.executePendingBindings()

        bindImage(binding.imgPost, post)
    }
}