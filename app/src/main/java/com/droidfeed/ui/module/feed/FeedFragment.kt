package com.droidfeed.ui.module.feed

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.databinding.FragmentNewsBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = FeedViewModel.Factory(newsRepo)
        viewModel = ViewModelProviders.of(this, factory).get(FeedViewModel::class.java)

        init()
        initAnimations()
        initObservables()
    }

    private fun initAnimations() {

        val anim1Y = SpringAnimation(binding.newsRecyclerView, DynamicAnimation.TRANSLATION_Y)
        anim1Y.addUpdateListener { _, value, _ -> anim1Y.animateToFinalPosition(value) }

    }

    private fun init() {
//        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        val layoutManager = LinearLayoutManager(activity)
        binding.newsRecyclerView.layoutManager = layoutManager
        binding.newsRecyclerView.overScrollMode = ScrollView.OVER_SCROLL_NEVER
        binding.newsRecyclerView.adapter = adapter
    }

    private fun initObservables() {
        viewModel.rssUiModelData.observe(this, Observer {
            if (it != null) adapter.addUiModels(it as Collection<BaseUiModelAlias>)
        })

        viewModel.articleClickEvent.observe(this, Observer {
            it?.link?.let { it1 -> customTab.showTab2(it1) }
        })

        viewModel.articleShareEvent.observe(this, Observer {
            startActivity(it)
        })

        viewModel.loadingFailedEvent.observe(this, Observer {

        })

    }
}