package com.droidfeed.ui.module.helpus

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.databinding.FragmentHelpusBinding
import com.droidfeed.util.extention.startActivity
import com.nytclient.ui.common.BaseFragment

/**
 * Created by Dogan Gulcan on 12/16/17.
 */
class HelpUsFragment : BaseFragment() {

    private lateinit var binding: FragmentHelpusBinding
    private lateinit var viewModel: HelpUsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHelpusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HelpUsViewModel::class.java)

        binding.onClickListener = viewModel.onClickListener

        initObservables()
    }

    private fun initObservables() {
        viewModel.contactDevEvent.observe(this, Observer {
            activity?.let { it1 -> it?.startActivity(it1) }
        })

    }


}