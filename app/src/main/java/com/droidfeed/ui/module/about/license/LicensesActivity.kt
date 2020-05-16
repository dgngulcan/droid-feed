package com.droidfeed.ui.module.about.license

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.databinding.ActivityLicenseBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelAdapter
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extension.observeEvent
import javax.inject.Inject

class LicensesActivity : BaseActivity() {

    @Inject lateinit var customTab: CustomTab
    @Inject lateinit var licenseAdapter: UIModelAdapter

    private val licensesViewModel: LicensesViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            val pinkColor = ContextCompat.getColor(
                this@LicensesActivity,
                R.color.pink
            )
            statusBarColor = pinkColor
            navigationBarColor = pinkColor
        }

        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityLicenseBinding>(
            this,
            R.layout.activity_license
        ).apply {
            toolbarTitle = getString(R.string.licenses)
            toolbarHomeNavClickListener = View.OnClickListener {
                licensesViewModel.onBackNavigation()
            }
            initRecyclerView(this)
        }

        subscribeOpenUrl()
        subscribeLicenseUIModels()
        subscribeOnBackNavigation()
    }

    private fun initRecyclerView(binding: ActivityLicenseBinding) {
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@LicensesActivity)
            licenseAdapter.layoutManager = linearLayoutManager
            layoutManager = linearLayoutManager
            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = licenseAdapter
        }

    }

    private fun subscribeOpenUrl() {
        licensesViewModel.openUrl.observeEvent(this) { url ->
            customTab.showTab(url)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun subscribeLicenseUIModels() {
        licensesViewModel.licenseUIModels.observe(this) { uiModels ->
            licenseAdapter.addUIModels(uiModels as List<BaseUIModelAlias>)
        }
    }

    private fun subscribeOnBackNavigation() {
        licensesViewModel.onBackNavigation.observe(this) {
            onBackPressed()
        }
    }
}