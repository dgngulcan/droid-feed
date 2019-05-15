package com.droidfeed.ui.module.about.licence

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.databinding.ActivityLicenceBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelAdapter
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.util.CustomTab
import com.droidfeed.util.event.EventObserver

class LicencesActivity : BaseActivity() {

    private val linearLayoutManager = LinearLayoutManager(this)
    private val licenceAdapter: UIModelAdapter by lazy {
        UIModelAdapter(
            this,
            linearLayoutManager
        )
    }

    private lateinit var licencesViewModel: LicencesViewModel

    private val customTab: CustomTab by lazy {
        CustomTab(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            val pinkColor = ContextCompat.getColor(
                this@LicencesActivity,
                R.color.pink
            )
            statusBarColor = pinkColor
            navigationBarColor = pinkColor
        }

        super.onCreate(savedInstanceState)

        licencesViewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(LicencesViewModel::class.java)

        DataBindingUtil.setContentView<ActivityLicenceBinding>(
            this,
            R.layout.activity_licence
        ).apply {
            toolbarTitle = getString(R.string.licences)
            toolbarHomeNavClickListener = View.OnClickListener {
                licencesViewModel.onBackNavigation()
            }

            recyclerView.apply {
                layoutManager = linearLayoutManager
                overScrollMode = View.OVER_SCROLL_NEVER
                adapter = licenceAdapter
            }
        }

        subscribeOpenUrl()
        subscribeLicenceUIModels()
        subscribeOnBackNavigation()
    }

    private fun subscribeOpenUrl() {
        licencesViewModel.openUrl.observe(this, EventObserver { url ->
            customTab.showTab(url)
        })
    }

    private fun subscribeLicenceUIModels() {
        licencesViewModel.licenceUIModels.observe(this, Observer { uiModels ->
            licenceAdapter.addUIModels(uiModels as List<BaseUIModelAlias>)
        })
    }

    private fun subscribeOnBackNavigation() {
        licencesViewModel.onBackNavigation.observe(this, Observer {
            onBackPressed()
        })
    }
}