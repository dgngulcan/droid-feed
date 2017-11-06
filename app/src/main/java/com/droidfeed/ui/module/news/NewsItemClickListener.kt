package com.droidfeed.ui.module.news

import android.view.View
import com.droidfeed.data.model.Article

/**
 * Created by Dogan Gulcan on 10/3/17.
 */
interface NewsItemClickListener {

    fun onItemClick(view: View, rssItem: Article)

    fun onShareClick(rssItem: Article)

    fun onBookmarkClick(rssItem: Article)

}