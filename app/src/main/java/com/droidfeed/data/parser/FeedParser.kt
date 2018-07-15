package com.droidfeed.data.parser

import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Channel
import com.droidfeed.data.model.Content
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.DebugUtils
import com.droidfeed.util.extention.skip
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 7/14/18.
 */
@Singleton
class FeedParser @Inject constructor(private var dateTimeUtils: DateTimeUtils) : XmlParser() {

    override fun parseArticles(parser: XmlPullParser): List<Article> {
        val articles = mutableListOf<Article>()

        try {
            parser.require(XmlPullParser.START_TAG, null, "feed")
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "entry" -> articles.add(parseArticle(parser))
                    else -> parser.skip()
                }
            }
        } catch (ignored: Exception) {
            DebugUtils.showStackTrace(ignored)
        }

        return articles
    }

    private fun parseArticle(parser: XmlPullParser): Article {
        val article = Article()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "author" -> article.channel = parseChannel(parser)
                "title" -> article.title = parser.nextText()
                "link" -> article.link = parseLink(parser)
                "updated" -> article.pubDateTimestamp = getPublishDate(parser.nextText())
                "media:group" -> article.content = parseMediaIntoArticle(parser)
                else -> parser.skip()
            }
        }

        return article
    }

    private fun parseChannel(parser: XmlPullParser): Channel {
        val channel = Channel()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "name" -> channel.title = parser.nextText()
                "uri" -> channel.link = parser.nextText()
                else -> parser.skip()
            }
        }

        return channel
    }

    private fun parseMediaIntoArticle(parser: XmlPullParser): Content {
        val content = Content()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "media:thumbnail" -> content.contentImage = parseLink(parser, "url")
                "media:description" -> content.content = parser.nextText()
                else -> parser.skip()
            }
        }

        return content
    }

    private fun getPublishDate(rawDate: String) = dateTimeUtils.getTimeStampFromDate(
        rawDate,
        DateTimeUtils.DateFormat.ATOM.format
    ) ?: 0

}