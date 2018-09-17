package com.droidfeed.data.parser

import com.droidfeed.data.model.Channel
import com.droidfeed.data.model.Content
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.extention.skipTag
import com.droidfeed.util.logStackTrace
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
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
                    else -> parser.skipTag()
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

        rssChannel.title = source.name

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "item" -> posts.add(readPost(parser, rssChannel, source))
                "atom:link" -> rssChannel.link = parseLink(parser)
                "image" -> rssChannel.imageUrl = parseChannelImage(parser)
                else -> parser.skipTag()
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
                else -> parser.skipTag()
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
                "description",
                "content:encoded" -> post.content = parsePostContent(parser.nextText())

                else -> parser.skipTag()
            }
        }

        return post
    }

    private fun getPublishDate(rawDate: String) =
        dateTimeUtils.getTimeStampFromDate(rawDate, DateTimeUtils.DateFormat.RSS.format) ?: 0

    private fun parsePostContent(contentText: String): Content {
        val doc = Jsoup.parse(contentText)
        val content = Content()

        val img = doc.selectFirst("img")
        val iFrame = doc.selectFirst("iframe")

        content.contentImage = when {
            img != null -> getImageFromDescription(img)
            iFrame != null -> getImageFromIFrame(iFrame.toString())
            else -> ""
        }

        return content
    }

    private fun getImageFromIFrame(frame: String): String {
        return when {
            frame.contains("image=") -> {
                val subFrame = frame.indexOf("image=") + 6
                URLDecoder.decode(
                    frame.subSequence(
                        subFrame,
                        frame.indexOf("&", subFrame)
                    ).toString(),
                    "UTF-8"
                )
            }
            else -> ""
        }
    }

    private fun getImageFromDescription(doc: Element): String {
        val img = doc.select("img").first()
        val absSrc = "abs:src"
        val src = "src"
        return when {
            img.hasAttr(absSrc) -> img.attr(absSrc)
            img.hasAttr(src) -> img.attr(src)
            else -> ""
        }
    }
}