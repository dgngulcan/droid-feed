package com.droidfeed.ui.module.main

import android.support.v4.app.Fragment
import com.droidfeed.R
import com.droidfeed.di.MainScope
import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.feed.FeedFragment
import com.droidfeed.ui.module.feed.FeedType
import com.droidfeed.ui.module.helpus.HelpUsFragment
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@MainScope
class MainNavController @Inject constructor(val activity: MainActivity) {

    private val fragmentManager = activity.supportFragmentManager
    private val containerId = R.id.fragmentContainer

    private val feedFragment: FeedFragment by lazy {
        FeedFragment.getInstance(FeedType.ALL)
    }

    private val bookmarkFragment: FeedFragment by lazy {
        FeedFragment.getInstance(FeedType.BOOKMARKS)
    }

    private val aboutFragment: AboutFragment by lazy {
        AboutFragment()
    }

    private val helpUsFragment: HelpUsFragment by lazy {
        HelpUsFragment()
    }

    fun openNewsFragment() {
        changeFragment(feedFragment)
    }

    fun openBookmarksFragment() {
        changeFragment(bookmarkFragment)
    }

    fun openAboutFragment() {
        changeFragment(aboutFragment)
    }

    fun openHelpUsFragment() {
        changeFragment(helpUsFragment)
    }

    private fun changeFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out)
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

    fun scrollToTop() {
        val currentFragment = fragmentManager.findFragmentById(containerId)
        when (currentFragment) {
            is FeedFragment -> currentFragment.scrollToTop()
        }

    }


}
