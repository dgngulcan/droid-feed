package com.droidfeed.ui.module.about

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.data.model.Licence
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.adapter.model.LicenceUiModel
import com.droidfeed.util.CustomTab
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_licence.*
import javax.inject.Inject

class LicencesFragment : DialogFragment() {

    private val adapter: UiModelAdapter by lazy { UiModelAdapter() }

    @Inject
    lateinit var customTab: CustomTab

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_licence, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        adapter.addUiModels(getLibrariesUiModels() as Collection<BaseUiModelAlias>)
    }

    private fun getLibrariesUiModels(): List<LicenceUiModel> {
        return getLibraries().map {
            LicenceUiModel(it) {
                customTab.showTab(it.url)
            }
        }
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