package com.droidfeed.ui.module.feed

import com.droidfeed.data.model.Article

interface ArticleClickListener {

    fun onItemClick(article: Article)

    fun onShareClick(article: Article)

    fun onBookmarkClick(article: Article)
}