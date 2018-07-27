package com.droidfeed.data.parser

import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Channel
import com.droidfeed.data.model.Content
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.extention.skip
import com.droidfeed.util.logStackTrace
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssParser @Inject constructor(private var dateTimeUtils: DateTimeUtils) : XmlParser() {

    override fun parseArticles(parser: XmlPullParser): List<Article> {
        val articles = mutableListOf<Article>()

        try {
            parser.require(XmlPullParser.START_TAG, null, "rss")
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "channel" -> articles.addAll(parseChannel(parser))
                    else -> parser.skip()
                }
            }
        } catch (ignored: Exception) {
            logStackTrace(ignored)
        }

        return articles
    }

    private fun parseChannel(parser: XmlPullParser): List<Article> {
        val rssChannel = Channel()
        val articles = mutableListOf<Article>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "item" -> articles.add(readArticle(parser, rssChannel))
                "title" -> rssChannel.title = parser.nextText()
                "atom:link" -> rssChannel.link = parseLink(parser)
                "image" -> rssChannel.imageUrl = parseChannelImage(parser)
                else -> parser.skip()
            }
        }

        return articles
    }

    private fun parseChannelImage(parser: XmlPullParser): String {
        var channelImage = ""
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "url" -> channelImage = parser.nextText()
                else -> parser.skip()
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

                else -> parser.skip()
            }
        }

        return article
    }

    private fun getPublishDate(rawDate: String) =
        dateTimeUtils.getTimeStampFromDate(rawDate, DateTimeUtils.DateFormat.RSS.format) ?: 0

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
}