package com.droidfeed.ui.common

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.droidfeed.util.AnalyticsUtil
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var analytics: AnalyticsUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    /**
     * Makes statusbar transparent.
     */
    protected fun setupTransparentStatusbar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    protected fun darkStatusbarTheme() {
        window.decorView.systemUiVisibility = 0
    }

    protected fun lightStatusbarTheme() {
        if (isMarshmallow()) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun isMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    override fun supportFragmentInjector(): DispatchingAndroidInjector<androidx.fragment.app.Fragment> =
        fragmentInjector
}
