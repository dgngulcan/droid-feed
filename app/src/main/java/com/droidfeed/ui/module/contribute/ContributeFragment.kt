package com.droidfeed.ui.module.contribute

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.databinding.FragmentContributeBinding
import com.droidfeed.util.CustomTab
import com.nytclient.ui.common.BaseFragment
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 12/16/17.
 */
class ContributeFragment : BaseFragment() {

    private lateinit var binding: FragmentContributeBinding
    private lateinit var viewModel: ContributeViewModel
    @Inject lateinit var customTab: CustomTab


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentContributeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ContributeViewModel::class.java)

        binding.onClickListener = viewModel.onClickListener

        initObservables()
    }

    private fun initObservables() {
        viewModel.contactDevEvent.observe(this, Observer {
            it?.let { it1 -> customTab.showTab(it1) }
        })

    }


}