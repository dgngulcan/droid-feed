package com.droidfeed.data

import android.util.Xml
import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Channel
import com.droidfeed.data.model.Content
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.DebugUtils
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.StringReader
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by Dogan Gulcan on 10/27/17.
 */
@Singleton
class RssXmlParser @Inject constructor(private var dateTimeUtils: DateTimeUtils) {

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(xml: String): ArrayList<Article> {
        val inputStream = StringReader(xml)

        inputStream.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream)
            try {
                parser.nextTag()
            } catch (e: Exception) {
                DebugUtils.showStackTrace(e)
            }
            return readFeed(parser)
        }
    }

    private fun readFeed(parser: XmlPullParser): ArrayList<Article> {
        val articles = ArrayList<Article>()

        try {
            parser.require(XmlPullParser.START_TAG, null, "rss")
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "channel" -> articles.addAll(parseChannel(parser))
                    else -> skip(parser)
                }
            }
        } catch (ignored: Exception) {
            DebugUtils.showStackTrace(ignored)
        }

        return articles
    }

    private fun parseChannel(parser: XmlPullParser): ArrayList<Article> {
        val rssChannel = Channel()
        val articles = ArrayList<Article>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "item" -> articles.add(readArticle(parser, rssChannel))
                "title" -> rssChannel.title = parser.nextText()
                "atom:link" -> rssChannel.link = parseChannelLink(parser)
                "image" -> rssChannel.imageUrl = parseChannelImage(parser)
                else -> skip(parser)
            }
        }

        return articles
    }

    private fun parseChannelLink(parser: XmlPullParser): String {
        val link = parser.getAttributeValue(
            XmlPullParser.NO_NAMESPACE,
            "href"
        )
        parser.nextTag()
        return link
    }

    private fun parseChannelImage(parser: XmlPullParser): String {
        var channelImage = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "url" -> channelImage = parser.nextText()
                else -> skip(parser)
            }
        }
        return channelImage
    }

    private fun readArticle(parser: XmlPullParser, rssChannel: Channel): Article {
        val article = Article()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            article.channel = rssChannel

            when (parser.name) {
                "title" -> article.title = parser.nextText()
                "dc:creator" -> article.author = parser.nextText()
                "link" -> article.link = parser.nextText()
                "pubDate" -> article.pubDateTimestamp = getPublishDate(parser.nextText())
                "content:encoded" -> article.content = parseArticleContent(parser.nextText())
                "description" -> article.content.contentImage =
                        getImageFromDescription(parser.nextText())

                else -> skip(parser)
            }
        }

        return article
    }

    private fun getPublishDate(rawDate: String) =
        dateTimeUtils.getTimeStampFromDate(rawDate) ?: 0

    private fun getImageFromDescription(descText: String): String {
        val doc = Jsoup.parse(descText)

        return try {
            doc.select("img").first().attr("abs:src")
        } catch (e: NullPointerException) {
            ""
        }
    }

    private fun parseArticleContent(contentText: String): Content {
        val doc = Jsoup.parse(contentText)
        val content = Content()

        try {
            val frame = doc.select("iframe")?.first()?.toString()

            content.contentImage =
                    if (frame != null && !frame.isBlank() && frame.contains("image=")) {
                        val subFrame = frame.indexOf("image=") + 6
                        URLDecoder.decode(
                            frame.subSequence(subFrame, frame.indexOf("&", subFrame))
                                .toString()
                        )

                    } else {
                        doc.select("img").first().attr("abs:src")
                    }

        } catch (ignored: NullPointerException) {
        }

        return content
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            return
        }

        try {
            var depth = 1
            while (depth != 0) {
                when (parser.next()) {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        } catch (e: Exception) {
            DebugUtils.showStackTrace(e)
        }
    }

}