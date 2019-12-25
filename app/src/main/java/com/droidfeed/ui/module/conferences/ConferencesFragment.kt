package com.droidfeed.ui.module.conferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.databinding.FragmentConferencesBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelAdapter
import com.droidfeed.ui.common.BaseFragment
import com.droidfeed.ui.common.CollapseScrollListener
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.util.CustomTab
import com.droidfeed.util.event.EventObserver
import javax.inject.Inject

class ConferencesFragment : BaseFragment("conferences") {

    @Inject lateinit var customTab: CustomTab

    private val conferencesViewModel: ConferencesViewModel by viewModels { viewModelFactory }
    private val mainViewModel: MainViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentConferencesBinding

    private val linearLayoutManager = LinearLayoutManager(context)
    private val uiModelAdapter: UIModelAdapter by lazy {
        UIModelAdapter(
            lifecycleScope,
            linearLayoutManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentConferencesBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            viewModel = conferencesViewModel
            lifecycleOwner = this@ConferencesFragment
        }

        subscribeOpenUrl()
        subscribeConferences()

        initFeed()

        return binding.root
    }

    private fun subscribeOpenUrl() {
        conferencesViewModel.openUrl.observe(viewLifecycleOwner, EventObserver { url ->
            customTab.showTab(url)
        })
    }

    private fun subscribeConferences() {
        conferencesViewModel.conferences.observe(viewLifecycleOwner, Observer { uiModels ->
            uiModelAdapter.addUIModels(uiModels as List<BaseUIModelAlias>)
        })
    }

    private fun initFeed() {
        binding.newsRecyclerView.apply {
            layoutManager = WrapContentLinearLayoutManager(requireContext())

            addOnScrollListener(CollapseScrollListener(lifecycleScope) {
                mainViewModel.onCollapseMenu()
            })

            swapAdapter(
                uiModelAdapter,
                false
            )
        }
    }
}