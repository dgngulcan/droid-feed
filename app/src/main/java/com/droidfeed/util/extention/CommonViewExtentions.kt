package com.droidfeed.util.extention

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.droidfeed.R

/**
 *  Commonly used extension functions.
 */

/**
 * Loads given image url into the ImageView via Glide.
 */
fun ImageView.loadImage(urlOrResource: Any) {
    Glide.with(context)
        .load(urlOrResource)
        .apply(
            RequestOptions()
                .error(context.getDrawable(R.drawable.ic_broken_image_black_24dp))
        )
        .into(this)
}

fun View.toggleVisibility(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}