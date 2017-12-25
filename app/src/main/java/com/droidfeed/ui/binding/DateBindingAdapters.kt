package com.droidfeed.ui.binding

import android.databinding.BindingAdapter
import android.text.format.DateUtils
import android.widget.TextView
import java.util.*
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 10/3/17.
 */
class DateBindingAdapters @Inject constructor() {

    @BindingAdapter("relativeDate")
    fun setRelativeDate(view: TextView, timeStamp: Long) {
        view.text = DateUtils.getRelativeTimeSpanString(
                timeStamp,
                Calendar.getInstance(TimeZone.getDefault()).timeInMillis,
                android.text.format.DateUtils.SECOND_IN_MILLIS)
    }

}