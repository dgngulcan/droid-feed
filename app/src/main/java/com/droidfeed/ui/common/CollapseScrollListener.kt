package com.droidfeed.ui.common

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class CollapseScrollListener(
    private val coroutineScope: CoroutineScope,
    private val collapseListener: () -> Unit
) : RecyclerView.OnScrollListener(),
    CoroutineScope by coroutineScope {
    private var scrolledAmount = 0

    private val scrollThreshold = 200L
    private val scrollDiminishingDelay = 100L

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        scrolledAmount += dy.absoluteValue

        if (scrolledAmount > scrollThreshold) {
            collapseListener()
            scrolledAmount = 0
        }

        launch {
            delay(scrollDiminishingDelay)
            scrolledAmount = 0
        }
    }
}