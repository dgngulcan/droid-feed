package com.droidfeed.ui.module.about.licence

import androidx.lifecycle.MutableLiveData
import com.droidfeed.data.model.Licence
import com.droidfeed.ui.adapter.model.LicenceUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LicencesViewModel @Inject constructor() : BaseViewModel() {

    val licenceUIModels = MutableLiveData<List<LicenceUIModel>>()
    val openUrl = MutableLiveData<Event<String>>()
    val onBackNavigation = MutableLiveData<Event<Unit>>()

    init {
        fillLicenceUIModels()
    }

    private fun fillLicenceUIModels() = launch(Dispatchers.IO) {
        val uiModels = getLicences().map { licence ->
            LicenceUIModel(licence) { url ->
                openUrl.postValue(Event(url))
            }
        }

        licenceUIModels.postValue(uiModels)
    }

    fun onBackNavigation() {
        onBackNavigation.postValue(Event(Unit))
    }

    private fun getLicences(): List<Licence> {
        return mutableListOf<Licence>()
            .apply {
                add(
                    Licence(
                        "Anyi Sun",
                        "DroidFeed application design, illustrations and animations are created by Anyi Sun",
                        "https://anyisun.com",
                        "https://creativecommons.org/licenses/by/4.0/"
                    )
                )
                add(
                    Licence(
                        "Dagger",
                        "A fast dependency injector for Android and Java.",
                        "https://github.com/google/dagger/",
                        "https://raw.githubusercontent.com/google/dagger/master/LICENSE.txt"
                    )
                )
                add(
                    Licence(
                        "Glide",
                        "Glide is a fast and efficient open source media management and " +
                                "contentImage loading framework for Android that wraps media decoding, " +
                                "memory and disk caching, and resource pooling into a simple and easy to " +
                                "use interface",
                        "https://github.com/bumptech/glide/",
                        "https://raw.githubusercontent.com/bumptech/glide/master/LICENSE"
                    )
                )
                add(
                    Licence(
                        "Lottie",
                        "Java library for working with real-world HTML",
                        "https://github.com/airbnb/lottie-android",
                        "https://raw.githubusercontent.com/airbnb/lottie-android/master/LICENSE"
                    )
                )
                add(
                    Licence(
                        "OkHttp",
                        "An HTTP & HTTP/2 client for Android and Java applications",
                        "https://github.com/square/okhttp/",
                        "https://raw.githubusercontent.com/square/okhttp/master/LICENSE.txt"
                    )
                )
                add(
                    Licence(
                        "Jsoup",
                        "Java library for working with real-world HTML",
                        "https://github.com/jhy/jsoup",
                        "https://raw.githubusercontent.com/jhy/jsoup/master/LICENSE"

                    )
                )
                add(
                    Licence(
                        "Retrofit",
                        "Type-safe HTTP client for Android and Java by Square, Inc.",
                        "https://github.com/square/retrofit",
                        "https://raw.githubusercontent.com/square/retrofit/master/LICENSE.txt"
                    )
                )
                add(
                    Licence(
                        "Henrique Rossatto",
                        "Hamburger Menu Icon",
                        "https://www.lottiefiles.com/henriqrossatto",
                        "https://creativecommons.org/licenses/by/4.0/"
                    )
                )
                add(
                    Licence(
                        "Mockito",
                        "Most popular Mocking framework for unit tests written in Java",
                        "https://site.mockito.org/",
                        "https://raw.githubusercontent.com/mockito/mockito/release/2.x/LICENSE"
                    )
                )
                add(
                    Licence(
                        "Detekt",
                        "Static code analysis for Kotlin",
                        "https://github.com/arturbosch/detekt",
                        "https://raw.githubusercontent.com/arturbosch/detekt/master/LICENSE"
                    )
                )
            }.sortedBy { licence ->
                licence.name
            }
    }

}