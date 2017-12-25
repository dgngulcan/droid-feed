package com.droidfeed.ui.module.about

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.databinding.FragmentAboutBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.util.CustomTab
import com.droidfeed.util.extention.startActivity
import com.nytclient.ui.common.BaseFragment
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 11/5/17.
 */
class AboutFragment : BaseFragment() {

    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: AboutViewModel

    private val adapter: UiModelAdapter by lazy { UiModelAdapter() }
    @Inject lateinit var customTab: CustomTab

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)

        binding.viewModel = viewModel
        binding.onClickListener = viewModel.aboutScreenClickListener

        init()
        initObservers()
    }

    private fun init() {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun initObservers() {
        viewModel.licenceUiModels.observe(this, Observer {
            adapter.addUiModels(it as Collection<BaseUiModelAlias>)
        })

        viewModel.rateAppEvent.observe(this, Observer {
            activity?.let { it1 -> it?.startActivity(it1) }
        })

        viewModel.contactDevEvent.observe(this, Observer {
            activity?.let { it1 -> it?.startActivity(it1) }
        })

        viewModel.shareAppEvent.observe(this, Observer {
            activity?.let { it1 -> it?.startActivity(it1) }
        })

        viewModel.licenceClickEvent.observe(this, Observer {
            it?.url?.let { it1 -> customTab.showTab(it1) }
        })
    }


}