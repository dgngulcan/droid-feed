package com.droidfeed.ui.module.about

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.droidfeed.BuildConfig
import com.droidfeed.R
import com.droidfeed.databinding.FragmentAboutBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.module.about.license.LicensesActivity
import com.droidfeed.util.AnimUtils.Companion.MEDIUM_ANIM_DURATION
import com.droidfeed.util.CustomTab
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.extension.observeEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("ValidFragment")

class AboutFragment : BaseFragment() {

    @Inject lateinit var customTab: CustomTab
    @Inject lateinit var intentProvider: IntentProvider

    private lateinit var binding: FragmentAboutBinding
    private val aboutViewModel: AboutViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            viewModel = aboutViewModel
            appVersion = getString(R.string.app_version, BuildConfig.VERSION_NAME)
            lifecycleOwner = this@AboutFragment
        }

        subscribeStartIntentEvent()
        subscribeOpenUrlEvent()
        subscribeOpenLicenseEvent()

        initAnimations()

        return binding.root
    }

    private fun subscribeOpenLicenseEvent() {
        aboutViewModel.openLicenses.observeEvent(viewLifecycleOwner) {
            Intent(context, LicensesActivity::class.java)
                .also(::startActivity)
        }
    }

    private fun subscribeOpenUrlEvent() {
        aboutViewModel.openUrl.observeEvent(viewLifecycleOwner) { url ->
            customTab.showTab(url)
        }
    }

    private fun subscribeStartIntentEvent() {
        aboutViewModel.startIntent.observeEvent(viewLifecycleOwner) { intentType ->
            startActivity(intentProvider.getIntent(intentType))
        }
    }

    private fun initAnimations() {
        binding.animView.setOnClickListener { view ->
            if (!(view as LottieAnimationView).isAnimating) {
                view.speed *= -1f
                view.resumeAnimation()
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            binding.animView.frame = 0
            delay(MEDIUM_ANIM_DURATION)
            binding.animView.resumeAnimation()
        }
    }
}