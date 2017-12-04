package com.droidfeed.ui.module.about

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.net.Uri
import com.droidfeed.BuildConfig
import com.droidfeed.data.model.Licence
import com.droidfeed.ui.adapter.model.LicenceUiModel
import com.droidfeed.util.DebugUtils
import com.droidfeed.util.workerThread
import com.nytclient.ui.common.BaseViewModel
import com.nytclient.ui.common.SingleLiveEvent


/**
 * Created by Dogan Gulcan on 11/5/17.
 */
class AboutViewModel : BaseViewModel() {

    val appVersion = BuildConfig.VERSION_NAME
    val rateAppEvent = SingleLiveEvent<Intent>()
    val contactDevEvent = SingleLiveEvent<Intent>()
    val shareAppEvent = SingleLiveEvent<Intent>()
    val licenceClickEvent = SingleLiveEvent<Licence>()

    val licenceUiModels: LiveData<List<LicenceUiModel>>
        get() = provideOpenSourceLibraryList()

    val rateAppIntent: Intent by lazy {
        try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))
        } catch (e: android.content.ActivityNotFoundException) {
            DebugUtils.showStackTrace(e)
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))
        }
    }

    val contactIntent: Intent by lazy {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:paxolite@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, "paxolite@gmail.com")
        intent.putExtra(Intent.EXTRA_SUBJECT, "About DroidFeed")
    }

    val shareIntent: Intent by lazy {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "This app helps me to keep up with Android" +
                "\n\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        intent.type = "text/plain"
        intent
    }

    val aboutScreenClickListener = object : AboutFragmentClickListener {
        override fun onRateAppClicked() {
            if (canUserClick) rateAppEvent.setValue(rateAppIntent)
        }

        override fun onContactClicked() {
            if (canUserClick) contactDevEvent.setValue(contactIntent)
        }

        override fun onShareClicked() {
            if (canUserClick) shareAppEvent.setValue(shareIntent)
        }
    }

    private val licenceClickListener = object : LicenceClickListener {
        override fun onClick(licence: Licence) {
            if (canUserClick) licenceClickEvent.setValue(licence)
        }
    }

    private fun provideOpenSourceLibraryList(): LiveData<List<LicenceUiModel>> {
        val licencesLiveData = MutableLiveData<List<LicenceUiModel>>()

        workerThread {
            val uiModels = ArrayList<LicenceUiModel>()
            getLicences().mapTo(uiModels) { LicenceUiModel(it, licenceClickListener) }
            licencesLiveData.postValue(uiModels)
        }

        return licencesLiveData
    }

    private fun getLicences(): List<Licence> {
        val licences = ArrayList<Licence>()

        licences.add(Licence("Glide", "Glide is a fast and efficient open source media management and contentImage loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.", "https://github.com/bumptech/glide"))
        licences.add(Licence("OkHttp", "An HTTP & HTTP/2 client for Android and Java applications.", "https://github.com/square/okhttp/"))
        licences.add(Licence("Dagger", "A fast dependency injector for Android and Java.", "https://github.com/google/dagger"))
        licences.add(Licence("Anko", "Anko is a Kotlin library which makes Android application development faster and easier. It makes your code clean and easy to read.", "https://github.com/Kotlin/anko"))
        licences.add(Licence("Jsoup", "Java library for working with real-world HTML.", "https://github.com/jhy/jsoup"))
        licences.add(Licence("Retrofit", "Type-safe HTTP client for Android and Java by Square, Inc.", "https://github.com/square/retrofit"))

        return licences
    }
}