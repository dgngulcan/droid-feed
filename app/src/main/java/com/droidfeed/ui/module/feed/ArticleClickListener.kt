package com.droidfeed.ui.module.feed

import com.droidfeed.data.model.Post

interface ArticleClickListener {

    fun onItemClick(article: Post)

    fun onShareClick(article: Post)

    fun onBookmarkClick(article: Post)
}