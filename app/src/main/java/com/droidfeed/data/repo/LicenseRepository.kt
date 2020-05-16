package com.droidfeed.data.repo

import com.droidfeed.data.model.License
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseRepository @Inject constructor() {

    fun getLicenses(): List<License> {
        return mutableListOf<License>()
            .apply {
                add(
                    License(
                        "Anyi Sun",
                        "DroidFeed application design, illustrations and animations are created by Anyi Sun",
                        "https://anyisun.com",
                        "https://creativecommons.org/licenses/by/4.0/"
                    )
                )
                add(
                    License(
                        "Dagger",
                        "A fast dependency injector for Android and Java.",
                        "https://github.com/google/dagger/",
                        "https://raw.githubusercontent.com/google/dagger/master/LICENSE.txt"
                    )
                )
                add(
                    License(
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
                    License(
                        "Lottie",
                        "Java library for working with real-world HTML",
                        "https://github.com/airbnb/lottie-android",
                        "https://raw.githubusercontent.com/airbnb/lottie-android/master/LICENSE"
                    )
                )
                add(
                    License(
                        "OkHttp",
                        "An HTTP & HTTP/2 client for Android and Java applications",
                        "https://github.com/square/okhttp/",
                        "https://raw.githubusercontent.com/square/okhttp/master/LICENSE.txt"
                    )
                )
                add(
                    License(
                        "Jsoup",
                        "Java library for working with real-world HTML",
                        "https://github.com/jhy/jsoup",
                        "https://raw.githubusercontent.com/jhy/jsoup/master/LICENSE"

                    )
                )
                add(
                    License(
                        "Retrofit",
                        "Type-safe HTTP client for Android and Java by Square, Inc.",
                        "https://github.com/square/retrofit",
                        "https://raw.githubusercontent.com/square/retrofit/master/LICENSE.txt"
                    )
                )
                add(
                    License(
                        "Henrique Rossatto",
                        "Hamburger Menu Icon",
                        "https://www.lottiefiles.com/henriqrossatto",
                        "https://creativecommons.org/licenses/by/4.0/"
                    )
                )
                add(
                    License(
                        "MockK",
                        "Mocking library for Kotlin",
                        "https://mockk.io/",
                        "https://github.com/mockk/mockk/blob/master/LICENSE"
                    )
                )

            }.sortedBy { it.name }
    }

}