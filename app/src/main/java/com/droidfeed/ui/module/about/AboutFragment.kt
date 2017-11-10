package com.droidfeed.ui.module.about

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.data.model.Licence
import com.droidfeed.databinding.FragmentAboutBinding
import com.nytclient.ui.common.BaseFragment
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Dogan Gulcan on 11/5/17.
 */
class AboutFragment : BaseFragment() {

    private lateinit var binding: FragmentAboutBinding
    private lateinit var viewModel: AboutViewModel

    @Inject
    @Named("LicenceList") lateinit var licenceList: List<Licence>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)

        binding.viewModel = viewModel
        binding.onClickListener = viewModel.clickListener


    }

}