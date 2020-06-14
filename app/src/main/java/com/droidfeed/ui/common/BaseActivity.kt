package com.droidfeed.ui.common

import android.annotation.SuppressLint
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.droidfeed.util.isMarshmallow


@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    protected fun setupFullScreenWindow() {
        if (isMarshmallow()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    protected fun darkStatusBarTheme() {
        if (isMarshmallow()) {
            window.decorView.systemUiVisibility = 0
        }
    }

    protected fun lightStatusBarTheme() {
        if (isMarshmallow()) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

}
