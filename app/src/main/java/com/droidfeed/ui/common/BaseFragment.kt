package com.droidfeed.ui.common

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment : androidx.fragment.app.Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}