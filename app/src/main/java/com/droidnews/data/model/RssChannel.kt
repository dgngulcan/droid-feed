package com.droidnews.data.model

/**
 * Created by Dogan Gulcan on 9/23/17.
 */
data class RssChannel(val title: String,

                      val description: String,

                      val link: String,

                      val lastBuildDate: String,

                      val rss: Array<RssItem>,

                      val image: RssImage,

                      val generator: String,

                      val webMaster: String)
