package com.droidfeed.data.parser

import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.util.extention.skipTag
import com.droidfeed.util.logThrowable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

abstract class XmlParser {

    abstract fun parsePosts(parser: XmlPullParser, source: Source): List<Post>

    /**
     * Parses and returns the channel title for given parser. The parser should contain valid
     * formatted feed inside.
     *
     * @param parser
     */
    abstract fun getChannelTitle(parser: XmlPullParser): String?

    /**
     * Obtains and returns a link from given parser accordingly to given attribute name
     *
     * @param parser
     * @param attributeName
     */
    fun parseLink(parser: XmlPullParser, attributeName: String = "href"): String {
        val link = parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, attributeName)
        parser.nextTag()
        return link
    }


    fun parseTags(
        parser: XmlPullParser,
        tagMap: Pair<String, (XmlPullParser) -> Unit>,
        isActive: () -> Boolean
    ) {
        try {
            while (parser.next() != XmlPullParser.END_TAG && isActive()) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                if (tagMap.first == parser.name) {
                    tagMap.second.invoke(parser)
                } else {
                    parser.skipTag()
                }
            }
        } catch (e: XmlPullParserException) {
            logThrowable(e)
        }
    }
}