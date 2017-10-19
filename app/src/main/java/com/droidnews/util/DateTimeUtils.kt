package com.droidnews.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/23/17.
 */
@Singleton
class DateTimeUtils {

    private val rssPubDateDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.US)

    fun getTimeStampFromDate(date: String, simpleDateFormat: SimpleDateFormat = rssPubDateDateFormat): Long? {
        var mDate: Date? = null

        try {
            mDate = simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()

        } finally {
            return mDate?.time
        }
    }
}