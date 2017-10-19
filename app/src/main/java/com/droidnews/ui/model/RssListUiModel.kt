package com.droidnews.ui.model

import com.droidnews.data.model.RssItem
import com.droidnews.ui.common.ListItemClickListener

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
data class RssListUiModel(
        val rssItem: RssItem,
        val onRssClickListener: ListItemClickListener<RssItem>)