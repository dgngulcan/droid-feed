package com.droidfeed.data.parser

import com.droidfeed.data.model.Channel
import com.droidfeed.data.model.Content
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.util.DateTimeUtils
import com.droidfeed.util.extention.skip
import com.droidfeed.util.logStackTrace
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedParser @Inject constructor(private var dateTimeUtils: DateTimeUtils) : XmlParser() {

    override fun parsePosts(parser: XmlPullParser, source: Source): List<Post> {
        val posts = mutableListOf<Post>()

        try {
            parser.require(XmlPullParser.START_TAG, null, "feed")
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "entry" -> posts.add(parsePost(parser, source))
                    else -> parser.skip()
                }
            }
        } catch (ignored: Exception) {
            logStackTrace(ignored)
        }

        return posts
    }

    private fun parsePost(parser: XmlPullParser, source: Source): Post {
        val post = Post()

        post.sourceId = source.id

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "author" -> post.channel = parseChannel(parser)
                "title" -> post.title = parser.nextText()
                "link" -> post.link = parseLink(parser)
                "updated" -> post.pubDateTimestamp = getPublishDate(parser.nextText())
                "media:group" -> post.content = parseMediaIntoPost(parser)
                else -> parser.skip()
            }
        }

        return post
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

    private fun parseMediaIntoPost(parser: XmlPullParser): Content {
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