package com.droidfeed.ui.module.contribute

import com.droidfeed.BuildConfig
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent
import javax.inject.Inject

class ContributeViewModel @Inject constructor() : BaseViewModel() {

    val openRepositoryEvent = SingleLiveEvent<String>()

    fun openRepository() {
        openRepositoryEvent.setValue(BuildConfig.DROIDFEED_GITHUB_URL)
    }
}