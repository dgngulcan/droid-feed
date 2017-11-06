package com.droidfeed.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/23/17.
 */
@Singleton
class DateTimeUtils @Inject constructor() {

    private val rssPubDateDateFormat = SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz", Locale.US)

    fun getTimeStampFromDate(date: String, simpleDateFormat: SimpleDateFormat = rssPubDateDateFormat): Long? {
        var mDate: Date? = null

        try {
            mDate = simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            DebugUtils.showStackTrace(e)
        } finally {
            return mDate?.time
        }
    }
}