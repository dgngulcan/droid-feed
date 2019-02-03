package com.droidfeed.ui.module.about

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieAnimationView
import com.droidfeed.BuildConfig
import com.droidfeed.R
import com.droidfeed.databinding.FragmentAboutBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.module.about.licence.LicencesActivity
import com.droidfeed.util.AnimUtils.Companion.MEDIUM_ANIM_DURATION
import com.droidfeed.util.CustomTab
import com.droidfeed.util.event.EventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("ValidFragment")

class AboutFragment : BaseFragment("about") {

    private lateinit var binding: FragmentAboutBinding
    private lateinit var aboutViewModel: AboutViewModel

    @Inject
    lateinit var customTab: CustomTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        aboutViewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(AboutViewModel::class.java)
    }

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
            setLifecycleOwner(this@AboutFragment)
        }

        subscribeStartIntentEvent()
        subscribeOpenUrlEvent()
        subscribeOpenLicenceEvent()

        initAnimations()

        return binding.root
    }

    private fun subscribeOpenLicenceEvent() {
        aboutViewModel.openLicences.observe(viewLifecycleOwner, EventObserver {
            Intent(context, LicencesActivity::class.java)
                .also { intent ->
                    startActivity(intent)
                }
        })
    }

    private fun subscribeOpenUrlEvent() {
        aboutViewModel.openUrl.observe(viewLifecycleOwner, EventObserver { url ->
            customTab.showTab(url)
        })
    }


    private fun subscribeStartIntentEvent() {
        aboutViewModel.startIntent.observe(viewLifecycleOwner, EventObserver { intent ->
            startActivity(intent)
        })
    }

    private fun initAnimations() {
        binding.animView.setOnClickListener { view ->
            if (!(view as LottieAnimationView).isAnimating) {
                view.speed *= -1f
                view.resumeAnimation()
            }
        }

        launch(Dispatchers.Main) {
            binding.animView.frame = 0
            delay(MEDIUM_ANIM_DURATION)
            binding.animView.resumeAnimation()
        }
    }


}