package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droidfeed.R
import com.droidfeed.data.model.Article
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.databinding.FragmentArticlesBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.DataInsertedCallback
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.common.WrapContentLinearLayoutManager
import com.droidfeed.util.CustomTab
import com.droidfeed.util.DebugUtils
import com.droidfeed.util.NetworkUtils
import com.nytclient.ui.common.BaseFragment
import org.jetbrains.anko.design.snackbar
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

    private val isEmptyBookmarked = ObservableBoolean(false)

    private lateinit var binding: FragmentArticlesBinding
    private var viewModel: FeedViewModel? = null
    private val adapter: UiModelAdapter by lazy { UiModelAdapter(dataInsertedCallback) }

    @Inject lateinit var newsRepo: RssRepo
    @Inject lateinit var customTab: CustomTab
    @Inject lateinit var networkUtils: NetworkUtils
    private val feedType by lazy {
        arguments?.getString(EXTRA_FEED_TYPE)?.let { FeedType.valueOf(it) }
    }

    private val dataInsertedCallback = object : DataInsertedCallback {
        override fun onUpdated() {
            if (feedType == FeedType.BOOKMARKS) {
                isEmptyBookmarked.set(adapter.itemCount == 0)
            }
            if (binding.swipeRefreshArticles.isRefreshing) {
                binding.swipeRefreshArticles.isRefreshing = false
            }
        }

        override fun onDataInserted(position: Int) {
            binding.newsRecyclerView.smoothScrollToPosition(position)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
        if (viewModel == null) {
            feedType?.let {
                val factory = FeedViewModel.Factory(newsRepo, it)
                viewModel = ViewModelProviders
                        .of(this, factory)
                        .get(FeedViewModel::class.java)
            }
        }

        binding.viewModel = viewModel
        binding.isEmptyBookmarked = isEmptyBookmarked

        initDataObservables()
    }

    private fun init() {
        val layoutManager = activity?.let { WrapContentLinearLayoutManager(it) }
        binding.newsRecyclerView.layoutManager = layoutManager
        (binding.newsRecyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        binding.newsRecyclerView.swapAdapter(adapter, true)

        binding.swipeRefreshArticles.setOnRefreshListener {
            viewModel?.onRefreshArticles()
        }

    }

    private fun initDataObservables() {
        DebugUtils.log("initDataObservables")
        viewModel?.rssUiModelData?.observe(this, Observer {
            adapter.addUiModels(it as Collection<BaseUiModelAlias>)
        })

        viewModel?.articleOpenDetail?.observe(this, Observer {
            it?.let(this::openArticleDetail)
        })

        viewModel?.articleShareEvent?.observe(this, Observer {
            startActivity(it)
        })

        viewModel?.loadingFailedEvent?.observe(this, Observer {
            if (it == true) snackbar(binding.root, R.string.error_obtaining_feed)
        })

    }

    private fun openArticleDetail(article: Article) {
        if (networkUtils.isDeviceConnectedToInternet()) {
            customTab.showTab(article.link)
        } else {
            snackbar(binding.root, R.string.info_no_internet)
        }
    }


}