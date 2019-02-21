package com.droidfeed.data.parser

import android.util.Xml
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.util.logThrowable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.StringReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsXmlParser @Inject constructor(
    private val rssParser: RssParser,
    private val feedParser: FeedParser
) {

    /**
     * Parses given RSS or Atom XML into a list of articles.
     *
     * @param xml as string
     * @return list of articles
     */
    fun parse(xml: String, source: Source): List<Post> {
        val inputStream = StringReader(xml)

        inputStream.use {
            val parser = Xml.newPullParser()

            try {
                parser.run {
                    setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                    setInput(inputStream)
                    nextTag()
                }
            } catch (e: XmlPullParserException) {
                logThrowable(e)
            }

            return when (parser.name) {
                "rss" -> rssParser.parsePosts(parser, source)
                "feed" -> feedParser.parsePosts(parser, source)
                else -> listOf()
            }
        }
    }
}