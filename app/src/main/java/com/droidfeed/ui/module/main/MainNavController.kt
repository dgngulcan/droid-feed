package com.droidfeed.ui.module.main

import android.support.v4.app.Fragment
import com.droidfeed.R
import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.feed.FeedFragment
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
class MainNavController @Inject constructor(activity: MainActivity) {

    private val fragmentManager = activity.supportFragmentManager
    private val containerId = R.id.fragmentContainer

    private val feedFragment: FeedFragment by lazy {
        FeedFragment()
    }

    private val aboutFragment: AboutFragment by lazy {
        AboutFragment()
    }


    fun openNewsFragment() {
        changeFragment(feedFragment)
    }

    fun openBookmarksFragment() {
    }

    fun openAboutFragment() {
        changeFragment(aboutFragment)
    }

    private fun changeFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out)
                .replace(containerId, fragment)
                .commitAllowingStateLoss()
    }

}
