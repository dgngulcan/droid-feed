package com.droidfeed.ui.binding

import android.databinding.BindingAdapter
import android.text.format.DateUtils
import android.widget.TextView
import com.droidfeed.util.DateTimeUtils
import java.util.*
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 10/3/17.
 */
class DateBindingAdapters @Inject constructor(val dateTimeUtils: DateTimeUtils) {


    @BindingAdapter("relativeDate")
    fun setRelativeDate(view: TextView, date: String) {
        view.text = dateTimeUtils.getTimeStampFromDate(date)?.let {
            DateUtils.getRelativeTimeSpanString(it,
                    Calendar.getInstance(TimeZone.getDefault()).timeInMillis,
                    android.text.format.DateUtils.SECOND_IN_MILLIS)
        }
    }


}