package com.droidfeed.ui.module.feed

import com.droidfeed.data.model.Post

interface PostClickListener {

    fun onItemClick(post: Post)

    fun onShareClick(post: Post)

    fun onBookmarkClick(post: Post)
}