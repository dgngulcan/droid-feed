package com.droidfeed.ui.module.about.licence

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.databinding.ActivityLicenceBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelAdapter
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extension.observeEvent
import javax.inject.Inject

class LicencesActivity : BaseActivity() {

    @Inject lateinit var customTab: CustomTab
    @Inject lateinit var licenceAdapter: UIModelAdapter

    private val licencesViewModel: LicencesViewModel by viewModels { viewModelFactory }

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

        DataBindingUtil.setContentView<ActivityLicenceBinding>(
            this,
            R.layout.activity_licence
        ).apply {
            toolbarTitle = getString(R.string.licences)
            toolbarHomeNavClickListener = View.OnClickListener {
                licencesViewModel.onBackNavigation()
            }
            initRecyclerView(this)
        }

        subscribeOpenUrl()
        subscribeLicenceUIModels()
        subscribeOnBackNavigation()
    }

    private fun initRecyclerView(binding: ActivityLicenceBinding) {
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@LicencesActivity)
            licenceAdapter.layoutManager = linearLayoutManager
            layoutManager = linearLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = licenceAdapter
        }

    }

    private fun subscribeOpenUrl() {
        licencesViewModel.openUrl.observeEvent(this) { url ->
            customTab.showTab(url)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun subscribeLicenceUIModels() {
        licencesViewModel.licenceUIModels.observe(this) { uiModels ->
            licenceAdapter.addUIModels(uiModels as List<BaseUIModelAlias>)
        }
    }

    private fun subscribeOnBackNavigation() {
        licencesViewModel.onBackNavigation.observe(this) {
            onBackPressed()
        }
    }
}