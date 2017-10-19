package com.droidnews.data.model

import org.simpleframework.xml.Element

/**
 * Created by Dogan Gulcan on 9/23/17.
 */
data class RssGuid(
        @Element(name = "guid_content")
        val content: String,

        @Element(name = "is_perma_link")
        val isPermaLink: String)