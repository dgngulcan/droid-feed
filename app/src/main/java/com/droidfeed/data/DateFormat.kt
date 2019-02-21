package com.droidfeed.data

import java.text.SimpleDateFormat
import java.util.*

enum class DateFormat(val format: SimpleDateFormat) {
    RSS(SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)),
    ATOM(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.ENGLISH))
}