package com.droidnews.ui.module.news

import com.droidnews.data.model.RssItem
import com.droidnews.ui.common.ListItemClickListener

/**
 * Created by Dogan Gulcan on 10/3/17.
 */
interface NewsItemClickListener : ListItemClickListener<RssItem> {

    fun onShareClick(rssItem: RssItem)

    fun onBookmarkClick(rssItem: RssItem)

}