package com.droidfeed.ui.module.contribute

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.droidfeed.BuildConfig
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import javax.inject.Inject

class ContributeViewModel @ViewModelInject constructor() : BaseViewModel() {

    val openRepositoryEvent = MutableLiveData<Event<String>>()

    fun openRepository() {
        openRepositoryEvent.postValue(Event(BuildConfig.DROIDFEED_GITHUB_URL))
    }
}