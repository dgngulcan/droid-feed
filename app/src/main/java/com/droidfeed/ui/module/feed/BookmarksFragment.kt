package com.droidfeed.ui.module.feed

import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.droidfeed.R
import com.droidfeed.data.model.Article

class BookmarksFragment : FeedFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.load(FeedType.BOOKMARKS)
        initObservables()
    }

    private fun initObservables() {
        viewModel.articleOnUnBookmark.observe(this@BookmarksFragment, Observer { article ->
            article?.let {
                showBookmarkUndoSnackbar(it)
                analytics.logBookmark(it.bookmarked == 1)
            }
        })
    }

    private fun showBookmarkUndoSnackbar(article: Article?) {
        article?.let {
            Snackbar.make(
                binding.root,
                R.string.info_bookmark_removed,
                Snackbar.LENGTH_LONG
            )
                .setActionTextColor(Color.YELLOW)
                .setAction(R.string.undo) {
                    viewModel.toggleBookmark(article)
                }
                .show()
        }
    }
}