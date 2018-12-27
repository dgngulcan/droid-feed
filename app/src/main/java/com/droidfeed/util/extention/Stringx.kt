package com.droidfeed.util.extention

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorInt
import com.droidfeed.util.logException
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Returns clickable spannable.
 *
 * @param toSpan text that will be clickable
 * @param color color of the clickable text
 * @param clickEvent click event listener
 */
fun String.getClickableSpan(
    toSpan: String,
    @ColorInt color: Int = 0,
    clickEvent: (View) -> Unit
): SpannableString {
    val signUpSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            clickEvent.invoke(widget)
        }
    }

    val spanStartIndex = this.indexOf(toSpan)
    val spanEndIndex = spanStartIndex + toSpan.length
    val span = SpannableString(this)
    span.setSpan(
        signUpSpan,
        spanStartIndex,
        spanEndIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    if (color != 0) {
        span.setSpan(
            ForegroundColorSpan(color),
            spanStartIndex,
            spanEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return span
}

/**
 * Returns the timestamp value of the given date.
 *
 * @param simpleDateFormat
 *
 * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT or null
 */
@Synchronized
fun String.asTimestamp(
    simpleDateFormat: SimpleDateFormat
): Long? {
    return try {
        val mDate = simpleDateFormat.parse(this)
        mDate?.time
    } catch (e: ParseException) {
        logException(e)
        0L
    }
}
