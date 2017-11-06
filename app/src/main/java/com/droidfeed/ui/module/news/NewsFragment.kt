package com.droidfeed.ui.module.news

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.databinding.FragmentNewsBinding
import com.droidfeed.ui.adapter.UiModelAdapter
import com.nytclient.ui.common.BaseFragment
import javax.inject.Inject

/**
 * Fragment responsible for news feed.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
class NewsFragment : BaseFragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel: NewsViewModel
    @Inject lateinit var newsRepo: RssRepo
    @Inject lateinit var adapter: UiModelAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = NewsViewModel.Factory(newsRepo)
        viewModel = ViewModelProviders.of(this, factory).get(NewsViewModel::class.java)

        init()
        initObservables()
    }

    private fun init() {
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.newsRecyclerView.adapter = adapter
    }

    private fun initObservables() {
        viewModel.rssUiModelData.observe(this, Observer {
            adapter.addUiModels(it)
        })

    }
}