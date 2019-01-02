package com.droidfeed.ui.module.about

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airbnb.lottie.LottieAnimationView
import com.droidfeed.BuildConfig
import com.droidfeed.R
import com.droidfeed.databinding.FragmentAboutBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.util.AnimUtils.Companion.MEDIUM_ANIM_DURATION
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extention.startActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("ValidFragment")

class AboutFragment : BaseFragment("about") {

    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: AboutViewModel

    @Inject
    lateinit var customTab: CustomTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(AboutViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.appVersion = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        init()

        subscribeAppRateEvent()
        subscribeContactDevEvent()
        subscribeShareAppEvent()
        subscribeOpenLinkEvent()
        subscribeOpenLicenceEvent()

        return binding.root
    }

    private fun init() {

        binding.animView.setOnClickListener {view ->
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

    private fun subscribeOpenLicenceEvent() {
        viewModel.openLicencesEvent.observe(viewLifecycleOwner, Observer {
            startActivity(Intent(context, LicencesActivity::class.java))
        })
    }

    private fun subscribeOpenLinkEvent() {
        viewModel.openLinkEvent.observe(viewLifecycleOwner, Observer { link ->
            link?.let { customTab.showTab(it) }
        })
    }

    private fun subscribeShareAppEvent() {
        viewModel.shareAppEvent.observe(viewLifecycleOwner, Observer { intent ->
            activity?.let { context ->
                intent?.startActivity(context)
                analytics.logShare("app")
            }
        })
    }

    private fun subscribeContactDevEvent() {
        viewModel.contactDevEvent.observe(viewLifecycleOwner, Observer { intent ->
            activity?.let { context -> intent?.startActivity(context) }
        })
    }

    private fun subscribeAppRateEvent() {
        viewModel.rateAppEvent.observe(viewLifecycleOwner, Observer { intent ->
            activity?.let { context ->
                intent?.startActivity(context)
                analytics.logAppRateClick()
            }
        })
    }
}