package com.droidfeed.util.extension

import com.droidfeed.util.logThrowable
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

/**
 * Skips a tag.
 */
fun XmlPullParser.skipTag() {
    when {
        eventType != XmlPullParser.START_TAG -> return
        else -> skip()
    }
}

private fun XmlPullParser.skip() {
    try {
        var depth = 1
        while (depth != 0) {
            when (next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    } catch (e: XmlPullParserException) {
        logThrowable(e)
    }
}