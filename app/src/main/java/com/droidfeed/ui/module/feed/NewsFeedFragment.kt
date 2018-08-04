package com.droidfeed.ui.module.feed

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import com.droidfeed.R

class NewsFeedFragment : FeedFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.load(FeedType.NEWS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.fragment_news_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}