package com.droidfeed.ui.module.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import com.droidfeed.BuildConfig
import com.droidfeed.data.model.Licence
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.model.LicenceUiModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent
import com.droidfeed.util.contactIntent
import com.droidfeed.util.rateAppIntent
import com.droidfeed.util.shareIntent
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AboutViewModel @Inject constructor() : BaseViewModel() {

    val appVersion = BuildConfig.VERSION_NAME
    val rateAppEvent = SingleLiveEvent<Intent>()
    val contactDevEvent = SingleLiveEvent<Intent>()
    val openLinkEvent = SingleLiveEvent<String>()
    val shareAppEvent = SingleLiveEvent<Intent>()

    val licenceUiModels by lazy { getLibrariesUiModels() }

    fun openContributionPage() {
        if (canClick) openLinkEvent.setValue(BuildConfig.DROIDFEED_GITHUB_URL)
    }

    fun openPlayStore() {
        if (canClick) rateAppEvent.setValue(rateAppIntent)
    }

    fun contactEmail() {
        if (canClick) contactDevEvent.setValue(contactIntent)
    }

    fun shareApp() {
        if (canClick) shareAppEvent.setValue(shareIntent)
    }

    fun openPrivacyPolicy() {
        if (canClick) openLinkEvent.setValue(BuildConfig.DROIDFEED_PRIVACY_POLICY)
    }

    private val libraryClickListener = object : UiModelClickListener<Licence> {
        override fun onClick(model: Licence) {
            if (canClick) openLinkEvent.setValue(model.url)
        }
    }

    private fun getLibrariesUiModels(): LiveData<List<LicenceUiModel>> {
        val licencesLiveData = MutableLiveData<List<LicenceUiModel>>()

        launch {
            val uiModels = ArrayList<LicenceUiModel>()
            getLibraries().mapTo(uiModels) { LicenceUiModel(it, libraryClickListener) }
            licencesLiveData.postValue(uiModels)
        }

        return licencesLiveData
    }

    private fun getLibraries(): List<Licence> {
        val licences = mutableListOf<Licence>()

        licences.add(
            Licence(
                "Android KTX",
                "A set of Kotlin extensions for Android app development.",
                "https://github.com/android/android-ktx/"
            )
        )
        licences.add(
            Licence(
                "Dagger",
                "A fast dependency injector for Android and Java.",
                "https://github.com/google/dagger/"
            )
        )
        licences.add(
            Licence(
                "Glide",
                "Glide is a fast and efficient open source media management and contentImage loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.",
                "https://github.com/bumptech/glide/"
            )
        )
        licences.add(
            Licence(
                "OkHttp",
                "An HTTP & HTTP/2 client for Android and Java applications.",
                "https://github.com/square/okhttp/"
            )
        )
        licences.add(
            Licence(
                "Jsoup",
                "Java library for working with real-world HTML.",
                "https://github.com/jhy/jsoup/"
            )
        )
        licences.add(
            Licence(
                "Retrofit",
                "Type-safe HTTP client for Android and Java by Square, Inc.",
                "https://github.com/square/retrofit"
            )
        )

        return licences
    }
}