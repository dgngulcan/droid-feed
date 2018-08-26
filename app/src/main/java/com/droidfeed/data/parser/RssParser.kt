package com.droidfeed.data.parser

import com.droidfeed.data.model.Channel
import com.droidfeed.data.model.Content
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.extention.skip
import com.droidfeed.util.logStackTrace
import org.jsoup.Jsoup
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssParser @Inject constructor(private var dateTimeUtils: DateTimeUtils) : XmlParser() {

    override fun parsePosts(parser: XmlPullParser, source: Source): List<Post> {
        val posts = mutableListOf<Post>()

        try {
            parser.require(XmlPullParser.START_TAG, null, "rss")
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "channel" -> posts.addAll(parseChannel(parser, source))
                    else -> parser.skip()
                }
            }
        } catch (e: XmlPullParserException) {
            logStackTrace(e)
        }

        return posts
    }

    private fun parseChannel(parser: XmlPullParser, source: Source): List<Post> {
        val rssChannel = Channel()
        val posts = mutableListOf<Post>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "item" -> posts.add(readPost(parser, rssChannel, source))
                "title" -> rssChannel.title = parser.nextText()
                "atom:link" -> rssChannel.link = parseLink(parser)
                "image" -> rssChannel.imageUrl = parseChannelImage(parser)
                else -> parser.skip()
            }
        }

        return posts
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

    private fun readPost(parser: XmlPullParser, channel: Channel, source: Source): Post {
        val post = Post()

        post.sourceId = source.id

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            post.channel = channel

            when (parser.name) {
                "title" -> post.title = parser.nextText()
                "dc:creator" -> post.author = parser.nextText()
                "link" -> post.link = parser.nextText()
                "pubDate" -> post.pubDateTimestamp = getPublishDate(parser.nextText())
                "content:encoded" -> post.content = parsePostContent(parser.nextText())
                "description" -> post.content.contentImage =
                        getImageFromDescription(parser.nextText())

                else -> parser.skip()
            }
        }

        return post
    }

    private fun getPublishDate(rawDate: String) =
        dateTimeUtils.getTimeStampFromDate(rawDate, DateTimeUtils.DateFormat.RSS.format) ?: 0

    private fun getImageFromDescription(descText: String): String {
        val doc = Jsoup.parse(descText)

        return when {
            doc.hasAttr("img") -> {
                val img = doc.select("img").first()
                val imgAttributeName = "abs:src"
                when {
                    img.hasAttr(imgAttributeName) -> img.attr(imgAttributeName)
                    else -> ""
                }
            }
            else -> ""
        }
    }

    private fun parsePostContent(contentText: String): Content {
        val doc = Jsoup.parse(contentText)
        val content = Content()

        val frame = doc.select("iframe")?.first()?.toString()

        content.contentImage = if (frame != null && !frame.isBlank() && frame.contains("image=")) {
            val subFrame = frame.indexOf("image=") + 6
            URLDecoder.decode(
                frame.subSequence(
                    subFrame,
                    frame.indexOf("&", subFrame)
                ).toString(),
                "UTF-8"
            )
        } else if (doc.hasAttr("img")) {
            doc.select("img").first().attr("abs:src")
        } else {
            ""
        }

        return content
    }
}