package com.droidfeed.ui.module.conferences

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Conference
import com.droidfeed.data.repo.ConferenceRepo
import com.droidfeed.ui.adapter.model.ConferenceUIModel
import com.droidfeed.ui.module.conferences.analytics.ConferencesScreenLogger
import com.droidfeed.util.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConferencesViewModel @Inject constructor(
    private val conferenceRepo: ConferenceRepo,
    private val logger: ConferencesScreenLogger
) : ViewModel() {

    val conferences = MutableLiveData<List<ConferenceUIModel>>()
    val isProgressVisible = MutableLiveData<Boolean>().apply { value = true }
    val openUrl = MutableLiveData<Event<String>>()

    init {
        refresh()
    }

    private fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        isProgressVisible.postValue(conferences.value?.isEmpty() == true)

        when (val dataStatus = conferenceRepo.getUpcoming()) {
            is DataStatus.Successful -> {
                val uiModels = dataStatus.data?.map { conference ->
                    createConferenceUIModel(conference)
                }
                conferences.postValue(uiModels)
            }
            is DataStatus.Failed -> {
            }
        }
    }

    private fun createConferenceUIModel(conference: Conference): ConferenceUIModel {
        return ConferenceUIModel(
            conference,
            onItemClick = { conf ->
                openUrl.postValue(Event(conf.url))
                logger.logConferenceClick()
            },
            onCFPClick = { conf ->
                openUrl.postValue(Event(conf.cfpUrl))
                logger.logCFPClick()
            })
    }
}