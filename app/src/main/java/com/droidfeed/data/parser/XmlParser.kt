package com.droidfeed.data.parser

import com.droidfeed.data.model.Article
import org.xmlpull.v1.XmlPullParser

/**
 * Created by Dogan Gulcan on 7/14/18.
 */
abstract class XmlParser {

    abstract fun parseArticles(parser: XmlPullParser): List<Article>

    fun parseLink(parser: XmlPullParser, attributeName: String = "href"): String {
        val link = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, attributeName)
        parser.nextTag()
        return link
    }

}