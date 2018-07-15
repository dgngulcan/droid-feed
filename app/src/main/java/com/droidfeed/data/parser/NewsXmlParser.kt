package com.droidfeed.data.parser

import android.util.Xml
import com.droidfeed.data.model.Article
import com.droidfeed.util.DebugUtils
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.StringReader
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by Dogan Gulcan on 10/27/17.
 */
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
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(xml: String): List<Article> {
        val inputStream = StringReader(xml)

        inputStream.use {
            val parser = Xml.newPullParser()

            try {
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                parser.setInput(inputStream)
                parser.nextTag()
            } catch (e: Exception) {
                DebugUtils.showStackTrace(e)
            }

            return when (parser.name) {
                "rss" -> rssParser.parseArticles(parser)
                "feed" -> feedParser.parseArticles(parser)
                else -> listOf()

            }
        }
    }

}