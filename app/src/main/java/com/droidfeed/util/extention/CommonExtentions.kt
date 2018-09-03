package com.droidfeed.util.extention

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.droidfeed.util.logConsole
import com.droidfeed.util.logStackTrace
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Checks if the device has internet access.
 *
 * @return true if the device is online
 */
fun Context.isOnline(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

/**
 * Starts an activity if the activity can be resolved.
 */
fun Intent.startActivity(activity: Activity) {
    if (this.resolveActivity(activity.packageManager) != null) {
        activity.startActivity(this)
    } else {
        logConsole("There are no activity can handle this intent")
    }
}

/**
 * Return a random integer.
 */
fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

/**
 * Skips a tag within the parser.
 */
fun XmlPullParser.skip() {
    when {
        eventType != XmlPullParser.START_TAG -> return
        else -> skipTag()
    }
}

private fun XmlPullParser.skipTag() {
    try {
        var depth = 1
        while (depth != 0) {
            when (next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    } catch (e: XmlPullParserException) {
        logStackTrace(e)
    }
}

fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val innerObserver = Observer<T> {
        value = it
        latch.countDown()
    }

    launch(UI) { observeForever(innerObserver) }
    latch.await(2, TimeUnit.SECONDS)

    return value
}


fun String.getClickableSpanned(
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
        span.setSpan(ForegroundColorSpan(color), spanStartIndex, spanEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    }

    return span
}
