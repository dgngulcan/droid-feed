package com.droidfeed.util.behavior

import android.content.Context
import android.support.annotation.Keep
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View

/**
 * Workaround AppBarLayout.Behavior for https://issuetracker.google.com/66996774
 *
 *
 * See https://gist.github.com/chrisbanes/8391b5adb9ee42180893300850ed02f2 for
 * example usage.
 *
 * Kotlinised by Erik Huizinga (github: @erikhuizinga).
 */
@Keep
class FixAppBarLayoutBehavior(
    context: Context?,
    attrs: AttributeSet?
) :
    AppBarLayout.Behavior(context, attrs) {

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
        stopNestedScrollIfNeeded(dyUnconsumed, child, target, type)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        stopNestedScrollIfNeeded(dy, child, target, type)
    }

    private fun stopNestedScrollIfNeeded(
        dy: Int,
        child: AppBarLayout,
        target: View,
        type: Int
    ) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            val currOffset = topAndBottomOffset

            if (dy < 0 && currOffset == 0 || dy > 0 && currOffset == -child.totalScrollRange) {
                ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH)
            }
        }
    }
}