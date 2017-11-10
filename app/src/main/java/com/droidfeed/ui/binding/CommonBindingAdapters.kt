package com.droidfeed.ui.binding

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.droidfeed.util.extention.loadUrl

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
