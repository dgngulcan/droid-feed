package com.droidfeed.ui.module.about

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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


/**
 * Created by Dogan Gulcan on 11/5/17.
 */
class AboutViewModel : BaseViewModel() {

    val appVersion = BuildConfig.VERSION_NAME
    val rateAppEvent = SingleLiveEvent<Intent>()
    val contactDevEvent = SingleLiveEvent<Intent>()
    val openLinkEvent = SingleLiveEvent<String>()
    val shareAppEvent = SingleLiveEvent<Intent>()

    val licenceUiModels by lazy { getLibrariesUiModels() }

    val aboutScreenClickListener = object : AboutFragmentClickListener {
        override fun onContributeClicked() {
            if (userCanClick) openLinkEvent.setValue("https://github.com/dgngulcan/droid-feed")
        }

        override fun onRateAppClicked() {
            if (userCanClick) rateAppEvent.setValue(rateAppIntent)
        }

        override fun onContactClicked() {
            if (userCanClick) contactDevEvent.setValue(contactIntent)
        }

        override fun onShareClicked() {
            if (userCanClick) shareAppEvent.setValue(shareIntent)
        }

        override fun onPrivacyPolicyClicked() {
            if (userCanClick) openLinkEvent.setValue("https://app.termly.io/document/privacy-policy/ab750f11-7f6d-499d-aafb-57697bd8aba0")
        }
    }

    private val libraryClickListener = object : UiModelClickListener<Licence> {
        override fun onClick(model: Licence) {
            if (userCanClick) openLinkEvent.setValue(model.url)
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
        val licences = ArrayList<Licence>()

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
                "Dagger",
                "A fast dependency injector for Android and Java.",
                "https://github.com/google/dagger/"
            )
        )
        licences.add(
            Licence(
                "Android KTX",
                "A set of Kotlin extensions for Android app development.",
                "https://github.com/android/android-ktx/"
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