package com.droidfeed.ui.module.conferences

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Conference
import com.droidfeed.data.repo.ConferenceRepo
import com.droidfeed.ui.adapter.model.ConferenceUIModel
import com.droidfeed.util.event.Event
import com.droidfeed.util.extension.postEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConferencesViewModel @Inject constructor(
    private val conferenceRepo: ConferenceRepo
) : ViewModel() {

    val conferences = MutableLiveData<List<ConferenceUIModel>>(emptyList())
    val isProgressVisible = MutableLiveData(true)
    val openUrl = MutableLiveData<Event<String>>()

    init {
        refresh()
    }

    private fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        isProgressVisible.postValue(conferences.value?.isEmpty() == true)

        when (val dataStatus = conferenceRepo.getUpcoming()) {
            is DataStatus.Successful -> {
                dataStatus.data
                    ?.map(::createConferenceUIModel)
                    .also(conferences::postValue)
            }
        }
        isProgressVisible.postValue(false)
    }

    private fun createConferenceUIModel(conference: Conference): ConferenceUIModel {
        return ConferenceUIModel(
            conference,
            onItemClick = { conf -> openUrl.postEvent(conf.url) },
            onCFPClick = { conf -> openUrl.postEvent(conf.cfpUrl) }
        )
    }
}