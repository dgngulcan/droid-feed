package com.droidfeed.ui.common

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.droidfeed.util.AnalyticsUtil
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment(private val viewTag: String) : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytics: AnalyticsUtil

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        activity?.let { analytics.logScreenView(it, viewTag) }
    }
}