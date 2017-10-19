package com.droidnews.ui.common

import android.view.View

/**
 * A generic click listener to be used withing ViewModels to pass object data with it's view to be
 * animated.
 *
 * Created by Dogan Gulcan on 9/30/17.
 */
interface ListItemClickListener<in T> {
    fun onItemClick(view: View, obj: T)
}