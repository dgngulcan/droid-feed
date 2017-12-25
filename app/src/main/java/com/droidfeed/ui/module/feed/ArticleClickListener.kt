package com.droidfeed.ui.module.feed

import com.droidfeed.data.model.Article

/**
 * Created by Dogan Gulcan on 10/3/17.
 */
interface ArticleClickListener {

    fun onItemClick(article: Article)

    fun onShareClick(article: Article)

    fun onBookmarkClick(article: Article)

}