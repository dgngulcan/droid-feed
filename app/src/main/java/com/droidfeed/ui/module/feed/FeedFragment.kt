package com.droidfeed.ui.module.feed

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.R
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.databinding.FragmentNewsBinding
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.util.CustomTab
import com.nytclient.ui.common.BaseFragment
import javax.inject.Inject


/**
 * Fragment responsible for news feed.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
class FeedFragment : BaseFragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var viewModel: FeedViewModel
    @Inject lateinit var newsRepo: RssRepo
    @Inject lateinit var adapter: UiModelAdapter

    private val customTab: CustomTab by lazy { CustomTab(activity as Activity) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = FeedViewModel.Factory(newsRepo)
        viewModel = ViewModelProviders.of(this, factory).get(FeedViewModel::class.java)

        init()
        initObservables()
    }

    private fun init() {
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.newsRecyclerView.layoutManager = layoutManager
        binding.newsRecyclerView.adapter = adapter
    }

    private fun initObservables() {
        viewModel.rssUiModelData.observe(this, Observer {
            adapter.addUiModels(it)
        })

        viewModel.articleClickEvent.observe(this, Observer {
            it?.link?.let { it1 -> customTab.showTab2(it1) }
        })

        viewModel.articleShareEvent.observe(this, Observer {
            startActivity(Intent.createChooser(it, getText(R.string.send_with)))
        })

    }
}