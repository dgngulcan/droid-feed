package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.data.model.Article
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.databinding.FragmentArticlesBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.adapter.model.ArticleUiModel
import com.droidfeed.util.CustomTab
import com.droidfeed.util.DebugUtils
import com.droidfeed.util.NetworkUtils
import com.nytclient.ui.common.BaseFragment
import javax.inject.Inject


/**
 * Fragment responsible for news feed.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
class FeedFragment : BaseFragment() {

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

    private lateinit var binding: FragmentArticlesBinding
    private var viewModel: FeedViewModel? = null
    private val adapter: UiModelAdapter by lazy { UiModelAdapter() }

    //    val customTab: CustomTab by lazy { CustomTab(activity!!) }
    @Inject lateinit var newsRepo: RssRepo
    @Inject lateinit var customTab: CustomTab
    @Inject lateinit var networkUtils: NetworkUtils


    private val feedObserver = Observer<List<ArticleUiModel>> {
        adapter.addUiModels(it as Collection<BaseUiModelAlias>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()

        if (viewModel == null) {
            val factory = FeedViewModel.Factory(newsRepo)

            viewModel = activity?.let { activity ->
                ViewModelProviders.of(activity, factory).get(FeedViewModel::class.java)
            }
        }

        initDataObservables()
    }

    private fun init() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.initialPrefetchItemCount = 3
        binding.newsRecyclerView.layoutManager = layoutManager
        (binding.newsRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        binding.newsRecyclerView.swapAdapter(adapter, true)
    }

    private fun initDataObservables() {
        DebugUtils.log("initDataObservables")
        arguments?.let {
            when (FeedType.valueOf(it.getString(EXTRA_FEED_TYPE))) {
                FeedType.ALL -> viewModel?.rssUiModelData?.observe(this, feedObserver)

                FeedType.BOOKMARKS ->
                    viewModel?.rssUiBookmarksModelData?.observe(this, feedObserver)
            }
        }

        viewModel?.articleOpenDetail?.observe(this, Observer {
            it?.let(this::openArticleDetail)
        })

        viewModel?.articleShareEvent?.observe(this, Observer {
            startActivity(it)
        })

        viewModel?.loadingFailedEvent?.observe(this, Observer {

        })

    }

    private fun openArticleDetail(article: Article) {

        if (networkUtils.isDeviceConnectedToInternet()) {
            customTab.showTab(article.link)
        }

    }


}