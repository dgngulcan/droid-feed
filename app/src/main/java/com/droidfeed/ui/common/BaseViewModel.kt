package com.droidfeed.ui.common

import androidx.lifecycle.ViewModel
import com.droidfeed.util.AnalyticsUtil
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject lateinit var analytics: AnalyticsUtil

}