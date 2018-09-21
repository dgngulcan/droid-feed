package com.droidfeed.ui.module.about

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.droidfeed.BuildConfig
import com.droidfeed.R
import com.droidfeed.databinding.FragmentAboutBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extention.startActivity
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
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

        init()
        initObservers()

        return binding.root
    }

    private fun init() {
        binding.txtAppVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        binding.animView.setOnClickListener {
            if (!binding.animView.isAnimating) {
                binding.animView.speed *= -1f
                binding.animView.resumeAnimation()
            }
        }

        binding.animView.setOnClickListener {
            if (!binding.animView.isAnimating) {
                binding.animView.speed *= -1f
                binding.animView.resumeAnimation()
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.animView.frame = 0
            delay(500)
            binding.animView.resumeAnimation()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initObservers() {
        viewModel.rateAppEvent.observe(viewLifecycleOwner, Observer {
            activity?.let { it1 ->
                it?.startActivity(it1)
                analytics.logAppRateClick()
            }
        })

        viewModel.contactDevEvent.observe(viewLifecycleOwner, Observer {
            activity?.let { it1 -> it?.startActivity(it1) }
        })

        viewModel.shareAppEvent.observe(viewLifecycleOwner, Observer {
            activity?.let { it1 ->
                it?.startActivity(it1)
                analytics.logShare("app")
            }
        })

        viewModel.openLinkEvent.observe(viewLifecycleOwner, Observer {
            it?.let { it1 -> customTab.showTab(it1) }
        })

        viewModel.openLibrariesEvent.observe(viewLifecycleOwner, Observer {
            startActivity(Intent(context, LicencesActivity::class.java))
        })
    }
}