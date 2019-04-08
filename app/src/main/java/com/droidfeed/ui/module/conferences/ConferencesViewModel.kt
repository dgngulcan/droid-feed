package com.droidfeed.ui.module.conferences

import androidx.lifecycle.MutableLiveData
import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Conference
import com.droidfeed.data.repo.ConferenceRepo
import com.droidfeed.ui.adapter.model.ConferenceUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConferencesViewModel @Inject constructor(
    private val conferenceRepo: ConferenceRepo
) : BaseViewModel() {

    val conferences = MutableLiveData<List<ConferenceUIModel>>()
    val isProgressVisible = MutableLiveData<Boolean>().apply { value = true }
    val openUrl = MutableLiveData<Event<String>>()

    init {
        refresh()
    }

    private fun refresh() = launch(Dispatchers.IO) {
        isProgressVisible.postValue(conferences.value?.isEmpty() == true)

        val dataStatus = conferenceRepo.getUpcoming()

        when (dataStatus) {
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
                analytics.logConferenceClick()
            },
            onCFPClick = { conf ->
                openUrl.postValue(Event(conf.cfpUrl))
                analytics.logCFPClick()
            })
    }
}