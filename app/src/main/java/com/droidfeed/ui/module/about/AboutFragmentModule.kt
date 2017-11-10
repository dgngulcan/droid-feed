package com.droidfeed.ui.module.about

import com.droidfeed.data.model.Licence
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 11/5/17.
 */
@Module()
class AboutFragmentModule {

    @Singleton
    @Provides
    @Named("LicenceList")
    fun provideOpenSourceLibraryList(): List<Licence> {
        val licences = ArrayList<Licence>()

        licences.add(Licence("Glide", "Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.", "https://github.com/bumptech/glide"))
        licences.add(Licence("OkHttp", "An HTTP & HTTP/2 client for Android and Java applications.", "https://github.com/square/okhttp/"))
        licences.add(Licence("Dagger", "A fast dependency injector for Android and Java.", "https://github.com/google/dagger"))
        licences.add(Licence("Anko", "Anko is a Kotlin library which makes Android application development faster and easier. It makes your code clean and easy to read.", "https://github.com/Kotlin/anko"))
        licences.add(Licence("Jsoup", "Java library for working with real-world HTML.", "https://github.com/jhy/jsoup"))
        licences.add(Licence("Retrofit", "Type-safe HTTP client for Android and Java by Square, Inc.", "https://github.com/square/retrofit"))

        return licences.toList()
    }
}