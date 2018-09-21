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
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class ContributeFragment : BaseFragment("contribute") {

    private lateinit var binding: FragmentContributeBinding
    private lateinit var viewModel: ContributeViewModel

    @Inject
    lateinit var customTab: CustomTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(ContributeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContributeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        init()
        initObservers()

        return binding.root
    }

    private fun init() {
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

    private fun initObservers() {
        viewModel.openRepositoryEvent.observe(viewLifecycleOwner, Observer { url ->
            url?.let { it1 ->
                customTab.showTab(it1)
            }
        })
    }
}