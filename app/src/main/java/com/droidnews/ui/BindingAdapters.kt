package com.droidnews.ui

import android.databinding.BindingAdapter
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.util.*

/**
 * Created by Dogan Gulcan on 9/30/17.
 */

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String) {
    imageView.loadUrl(url)
}

@BindingAdapter("visibilityToggle")
fun visibilityToggle(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}


