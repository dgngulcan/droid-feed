package com.droidfeed.ui.module.about

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.net.Uri
import android.view.View
import com.droidfeed.BuildConfig
import com.droidfeed.data.model.Licence
import com.droidfeed.ui.adapter.model.LicenceUiModel
import com.nytclient.ui.common.BaseViewModel
import com.nytclient.ui.common.SingleLiveEvent
import org.jetbrains.anko.coroutines.experimental.bg


/**
 * Created by Dogan Gulcan on 11/5/17.
 */
class AboutViewModel : BaseViewModel() {

    val appVersion = BuildConfig.VERSION_NAME
    val rateAppEvent = SingleLiveEvent<Intent>()
    val contactDevEvent = SingleLiveEvent<Intent>()
    val shareAppEvent = SingleLiveEvent<Intent>()

    val licenceUiModels: LiveData<List<LicenceUiModel>>
        get() = provideOpenSourceLibraryList()

    val aboutScreenClickListener = object : AboutFragmentClickListener {
        override fun onRateAppClicked() {
            rateAppEvent.setValue(getRateAppIntent())
        }

        override fun onContactClicked() {
            contactDevEvent.setValue(getContactIntent())
        }

        override fun onShareClicked() {
            shareAppEvent.setValue(getShareAppIntent())
        }
    }

    private val licenceClickListener = View.OnClickListener {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getRateAppIntent(): Intent {
        return try {
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))
        } catch (anfe: android.content.ActivityNotFoundException) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))
        }
    }

    private fun getContactIntent(): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:paxolite@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, "paxolite@gmail.com")
        intent.putExtra(Intent.EXTRA_SUBJECT, "About DroidFeed")
        return intent
    }

    private fun getShareAppIntent(): Intent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This app helps me to keep up with Android" +
                "\n\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        sendIntent.type = "text/plain"
        return sendIntent
    }

    private fun provideOpenSourceLibraryList(): LiveData<List<LicenceUiModel>> {
        val licencesLiveData = MutableLiveData<List<LicenceUiModel>>()

        bg {
            val uiModels = ArrayList<LicenceUiModel>()
            getLicences().mapTo(uiModels) { LicenceUiModel(it, licenceClickListener) }
            licencesLiveData.postValue(uiModels)
        }

        return licencesLiveData
    }

    private fun getLicences(): List<Licence> {
        val licences = ArrayList<Licence>()

        licences.add(Licence("Glide", "Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.", "https://github.com/bumptech/glide"))
        licences.add(Licence("OkHttp", "An HTTP & HTTP/2 client for Android and Java applications.", "https://github.com/square/okhttp/"))
        licences.add(Licence("Dagger", "A fast dependency injector for Android and Java.", "https://github.com/google/dagger"))
        licences.add(Licence("Anko", "Anko is a Kotlin library which makes Android application development faster and easier. It makes your code clean and easy to read.", "https://github.com/Kotlin/anko"))
        licences.add(Licence("Jsoup", "Java library for working with real-world HTML.", "https://github.com/jhy/jsoup"))
        licences.add(Licence("Retrofit", "Type-safe HTTP client for Android and Java by Square, Inc.", "https://github.com/square/retrofit"))

        return licences
    }
}