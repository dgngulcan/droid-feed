package com.droidfeed.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateTimeUtils @Inject constructor() {

    enum class DateFormat(val format: SimpleDateFormat) {
        RSS(SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)),
        ATOM(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.ENGLISH))
    }

    @Synchronized
    fun getTimeStampFromDate(
        date: String,
        simpleDateFormat: SimpleDateFormat
    ): Long? {
        var mDate: Date? = null

        try {
            mDate = simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            logStackTrace(e)
        } finally {
            return mDate?.time
        }
    }
}