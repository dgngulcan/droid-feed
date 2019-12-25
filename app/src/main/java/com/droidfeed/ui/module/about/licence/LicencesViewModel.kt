package com.droidfeed.ui.module.about.licence

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.droidfeed.ui.adapter.model.LicenceUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.data.repo.LicenceRepository
import com.droidfeed.util.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LicencesViewModel @Inject constructor(
    private val licenceRepository: LicenceRepository
) : BaseViewModel() {

    val licenceUIModels = MutableLiveData<List<LicenceUIModel>>()
    val openUrl = MutableLiveData<Event<String>>()
    val onBackNavigation = MutableLiveData<Event<Unit>>()

    init {
        fillLicenceUIModels()
    }

    private fun fillLicenceUIModels() = viewModelScope.launch(Dispatchers.IO) {
        val uiModels = licenceRepository.getLicences().map { licence ->
            LicenceUIModel(licence) { url ->
                openUrl.postValue(Event(url))
            }
        }

        licenceUIModels.postValue(uiModels)
    }

    fun onBackNavigation() {
        onBackNavigation.postValue(Event(Unit))
    }

}