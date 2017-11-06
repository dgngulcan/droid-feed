package com.droidfeed.data

import android.util.Xml
import com.droidfeed.data.model.Article
import com.droidfeed.data.model.Channel
import com.droidfeed.util.DebugUtils
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.StringReader


/**
 *
 * Created by Dogan Gulcan on 10/27/17.
 */
class RssXmlParser {

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(xml: String): ArrayList<Article> {
        val inputStream = StringReader(xml)

        inputStream.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream)
            parser.nextTag()
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
        var rssChannel = Channel()
        val articles = ArrayList<Article>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "item" -> articles.add(readArticle(parser, rssChannel))
                "image" -> rssChannel = parseChannelImage(parser)
                else -> skip(parser)
            }
        }
        return articles
    }

    private fun parseChannelImage(parser: XmlPullParser): Channel {
        val rssChannel = Channel()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "url" -> rssChannel.imageUrl = parser.nextText()
                "title" -> rssChannel.title = parser.nextText()
                "link" -> rssChannel.link = parser.nextText()
                else -> skip(parser)
            }
        }
        return rssChannel
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
                "pubDate" -> article.pubDate = parser.nextText()
                "content:encoded" -> {
                    article.content = parser.nextText()
                    article.description = Jsoup.parse(article.content).text().substring(0, 100)
                    article.image = parseContentImage(article.content)
                }
                else -> skip(parser)
            }
        }

        return article
    }

    private fun parseContentImage(content: String): String {
        val doc = Jsoup.parse(content)
        var pic = ""

        try {
            //choose the first image found in the article
            pic = doc.select("img").first().attr("abs:src")
        } catch (e: NullPointerException) {
            DebugUtils.showStackTrace(e, "Rss article does not have an image")
        }

        return pic
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
        } catch (ignored: Exception) {
            DebugUtils.showStackTrace(ignored)
        }
    }
}