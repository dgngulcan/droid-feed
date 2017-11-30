package com.droidfeed.ui.module.feed

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var viewModel: FeedViewModel? = null
    private val adapter: UiModelAdapter by lazy { UiModelAdapter() }
    private val customTab: CustomTab by lazy { CustomTab(activity as Activity) }

    @Inject lateinit var newsRepo: RssRepo

    companion object {
        private val EXTRA_FEED_TYPE = "feed_type"

        fun getInstance(feedType: FeedType): FeedFragment {
            val feedFragment = FeedFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_FEED_TYPE, feedType.name)
            feedFragment.arguments = bundle
            return feedFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()

        if (viewModel == null) {
            arguments?.let {
                val factory = FeedViewModel.Factory(newsRepo,
                        FeedType.valueOf(it.getString(EXTRA_FEED_TYPE)))

                viewModel = activity?.let { it1 ->
                    ViewModelProviders.of(it1, factory)
                            .get(FeedViewModel::class.java)
                }
            }

            initAnimations()
            initDataObservables()
        }

    }

    private fun initAnimations() {
//        val anim1Y = SpringAnimation(binding.newsRecyclerView, DynamicAnimation.TRANSLATION_Y)
//        anim1Y.addUpdateListener { _, value, _ -> anim1Y.animateToFinalPosition(value) }
    }

    private fun init() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.initialPrefetchItemCount = 3
        binding.newsRecyclerView.layoutManager = layoutManager
        binding.newsRecyclerView.adapter = adapter
    }

    private fun initDataObservables() {
        arguments?.let {
            when (FeedType.valueOf(it.getString(EXTRA_FEED_TYPE))) {
                FeedType.ALL -> {
                    viewModel?.rssUiModelData?.observe(this, Observer {
                        if (it != null) adapter.addUiModels(it as Collection<BaseUiModelAlias>)
                    })
                }
                FeedType.BOOKMARKS -> {

                    viewModel?.rssUiBookmarksModelData?.observe(this, Observer {
                        if (it != null) adapter.addUiModels(it as Collection<BaseUiModelAlias>)
                    })
                }

            }
        }
//        viewModel?.rssUiModelData?.observe(this, Observer {
//            if (it != null) adapter.addUiModels(it as Collection<BaseUiModelAlias>)
//        })

        viewModel?.articleClickEvent?.observe(this, Observer {
            it?.link?.let { it1 -> customTab.showTab(it1) }
        })

        viewModel?.articleShareEvent?.observe(this, Observer {
            startActivity(it)
        })

        viewModel?.loadingFailedEvent?.observe(this, Observer {

        })

    }

}