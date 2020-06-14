package com.droidfeed.ui.module.about.license

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.droidfeed.ui.adapter.model.LicenseUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.data.repo.LicenseRepository
import com.droidfeed.util.event.Event
import com.droidfeed.util.extension.postEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LicensesViewModel @ViewModelInject constructor(
    private val licenseRepository: LicenseRepository
) : BaseViewModel() {

    val licenseUIModels = MutableLiveData<List<LicenseUIModel>>()
    val openUrl = MutableLiveData<Event<String>>()
    val onBackNavigation = MutableLiveData<Event<Unit>>()

    init {
        fillLicenseUIModels()
    }

    private fun fillLicenseUIModels() = viewModelScope.launch(Dispatchers.IO) {
        val uiModels = licenseRepository.getLicenses().map { license ->
            LicenseUIModel(license) { url ->
                openUrl.postEvent(url)
            }
        }

        licenseUIModels.postValue(uiModels)
    }

    fun onBackNavigation() {
        onBackNavigation.postEvent(Unit)
    }

}