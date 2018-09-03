package com.droidfeed.ui.module.about

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.data.model.Attribution
import com.droidfeed.data.model.Licence
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.adapter.model.LicenceUiModel
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.util.CustomTab
import kotlinx.android.synthetic.main.activity_licence.*

class LicencesActivity : BaseActivity() {

    private val adapter: UiModelAdapter by lazy { UiModelAdapter() }

    private val customTab: CustomTab by lazy { CustomTab(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isMarshmallow()) {
            setupTransparentStatusbar()
        }
        window.statusBarColor = ContextCompat.getColor(this@LicencesActivity, R.color.pink)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_licence)
        init()
    }

    private fun init() {
        val libraries = getLibrariesUiModels() as Collection<BaseUiModelAlias>

        adapter.addUiModels(libraries)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        recyclerView.adapter = adapter

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

//    private fun getAttributionUiModels(): List<LicenceUiModel> {
//        return getAttributions().map {
//            LicenceUiModel(it) {
//                customTab.showTab(it.url)
//            }
//        }
//    }

    private fun getAttributions(): List<Attribution> {
        val attributions = mutableListOf<Attribution>()

        attributions.add(
            Attribution(
                "Henrique Rossatto ",
                "Hamburget Menu Icon",
                listOf("https://www.lottiefiles.com/henriqrossatto")
            )
        )

        return attributions
    }

    private fun getLibrariesUiModels(): List<LicenceUiModel> {
        return getLibraries().map { licence ->
            LicenceUiModel(licence) { url ->
                customTab.showTab(url)
            }
        }
    }

    private fun getLibraries(): List<Licence> {
        val licences = mutableListOf<Licence>()

        licences.add(
            Licence(
                "Android KTX",
                "A set of Kotlin extensions for Android app development.",
                "https://github.com/android/android-ktx/",
                "https://raw.githubusercontent.com/android/android-ktx/master/LICENSE.txt"
            )
        )
        licences.add(
            Licence(
                "Dagger",
                "A fast dependency injector for Android and Java.",
                "https://github.com/google/dagger/",
                "https://raw.githubusercontent.com/google/dagger/master/LICENSE.txt"
            )
        )
        licences.add(
            Licence(
                "Glide",
                "Glide is a fast and efficient open source media management and contentImage loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.",
                "https://github.com/bumptech/glide/",
                "https://raw.githubusercontent.com/bumptech/glide/master/LICENSE"
            )
        )
        licences.add(
            Licence(
                "Lottie",
                "Java library for working with real-world HTML.",
                "https://github.com/airbnb/lottie-android",
                "https://raw.githubusercontent.com/airbnb/lottie-android/master/LICENSE"
            )
        )
        licences.add(
            Licence(
                "OkHttp",
                "An HTTP & HTTP/2 client for Android and Java applications.",
                "https://github.com/square/okhttp/",
                "https://raw.githubusercontent.com/square/okhttp/master/LICENSE.txt"
            )
        )
        licences.add(
            Licence(
                "Jsoup",
                "Java library for working with real-world HTML.",
                "https://raw.githubusercontent.com/jhy/jsoup/master/LICENSE"
            )
        )
        licences.add(
            Licence(
                "Retrofit",
                "Type-safe HTTP client for Android and Java by Square, Inc.",
                "https://github.com/square/retrofit",
                "https://raw.githubusercontent.com/square/retrofit/master/LICENSE.txt"
            )
        )

        return licences
    }
}