package com.droidfeed.ui.binding

import android.text.format.DateUtils
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.droidfeed.R
import java.util.*

@BindingAdapter("avdImageResource")
fun avdImageResource(
    imageView: ImageView,
    avdImageResource: Int
) {
    imageView.setImageResource(avdImageResource)
}

@BindingAdapter("visibilityToggle")
fun visibilityToggle(
    view: View,
    show: Boolean
) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("loadHtml")
fun loadHtml(
    webView: WebView,
    htmlContent: String
) {
    if (htmlContent.isNotBlank()) {
        webView.loadData(htmlContent, "text/html", "UTF-8")
    }
}

@BindingAdapter("displayUrl")
fun displayUrl(
    webView: WebView,
    url: String
) {
    webView.loadUrl(url)
}

@BindingAdapter("relativeDate")
fun setRelativeTime(
    view: TextView,
    timeStamp: Long
) {
    view.text = DateUtils.getRelativeTimeSpanString(
        timeStamp,
        Calendar.getInstance(TimeZone.getDefault()).timeInMillis,
        DateUtils.SECOND_IN_MILLIS
    )
}

@BindingAdapter(
    value = ["publisher",
        "timestamp"],
    requireAll = true
)
fun setRelativeDate(
    view: TextView,
    publisher: String?,
    timestamp: Long?
) {
    val date = if (timestamp == null) {
        ""
    } else {
        DateUtils.getRelativeTimeSpanString(
            timestamp,
            Calendar.getInstance(TimeZone.getDefault()).timeInMillis,
            android.text.format.DateUtils.SECOND_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_ALL
        )
    }

    view.text = view.context.getString(
        R.string.publisher_time,
        publisher ?: "",
        date.toString()
    )
}