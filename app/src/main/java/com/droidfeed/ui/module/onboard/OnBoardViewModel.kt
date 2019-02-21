package com.droidfeed.ui.module.onboard

import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import com.droidfeed.BuildConfig
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

class OnBoardViewModel @Inject constructor(
    private val sourceRepo: SourceRepo
) : BaseViewModel() {

    val backgroundImageId = R.drawable.onboard_bg

    val isProgressVisible = MutableLiveData<Boolean>().apply { value = false }
    val isContinueButtonEnabled = MutableLiveData<Boolean>().apply { value = false }
    val isAgreementCBEnabled = MutableLiveData<Boolean>().apply { value = true }

    val openUrl = MutableLiveData<Event<String>>()
    val openMainActivity = MutableLiveData<Event<Unit>>()
    val showSnackBar = MutableLiveData<Event<@DrawableRes Int>>()

    private var isSourceListPulled = false
    private var isPendingNavigation = false
    private var pullSourceJob = Job()

    init {
        pullSourceJob = pullSources() /* news sources are pulled on first open */
    }

    fun onTermsOfUseClicked() {
        openUrl.postValue(Event(BuildConfig.DROIDFEED_PRIVACY_POLICY))
    }

    fun onContinueClicked() {
        if (isSourceListPulled) {
            openMainActivity.postValue(Event(Unit))
        } else {
            if (!pullSourceJob.isActive) {
                pullSourceJob = pullSources()
            }

            postLoadingState(true)
        }

        isPendingNavigation = !isSourceListPulled
    }

    private fun pullSources() = launch(Dispatchers.IO) {
        val result = sourceRepo.pull()

        when (result) {
            is DataStatus.Successful -> {
                sourceRepo.insert(result.data ?: emptyList())
                isSourceListPulled = true

                if (isPendingNavigation) {
                    postLoadingState(false)
                    onContinueClicked()
                }
            }
            is DataStatus.Failed -> {
                isSourceListPulled = false

                if (isPendingNavigation) {
                    postLoadingState(false)

                    when (result.throwable) {
                        is UnknownHostException -> {
                            showSnackBar.postValue(Event(R.string.info_no_internet))
                        }
                        else -> {
                            if (isPendingNavigation) {
                                showSnackBar.postValue(Event(R.string.error_cannot_obtain_sources))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun postLoadingState(isLoading: Boolean) {
        isContinueButtonEnabled.postValue(!isLoading)
        isAgreementCBEnabled.postValue(!isLoading)
        isProgressVisible.postValue(isLoading)
    }
}