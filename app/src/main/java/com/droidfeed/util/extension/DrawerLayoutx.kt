package com.droidfeed.util.extension

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout

fun DrawerLayout.addOnDrawerClosedListener(listener: (View) -> Unit) {
    addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(newState: Int) {
        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        }

        override fun onDrawerClosed(drawerView: View) {
            listener(drawerView)
        }

        override fun onDrawerOpened(drawerView: View) {
        }
    })
}