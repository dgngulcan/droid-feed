package com.droidfeed.ui.module.contribute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.droidfeed.databinding.FragmentContributeBinding
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.util.CustomTab
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 12/16/17.
 */
class ContributeFragment : BaseFragment() {

    private lateinit var binding: FragmentContributeBinding
    private lateinit var viewModel: ContributeViewModel

    @Inject
    lateinit var customTab: CustomTab

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContributeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(ContributeViewModel::class.java)

        binding.viewModel = viewModel

        init()
        initObservables()
    }

    private fun init() {
        launch(UI) {
            binding.animView.frame = 0
            delay(500)
            binding.animView.playAnimation()
        }
    }

    private fun initObservables() {
        viewModel.contactDevEvent.observe(this, Observer {
            it?.let { it1 -> customTab.showTab(it1) }
        })
    }
}