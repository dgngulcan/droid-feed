package com.droidfeed.ui.module.main

import com.droidfeed.R
import com.droidfeed.di.MainScope
import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.contribute.ContributeFragment
import com.droidfeed.ui.module.feed.FeedFragment
import com.droidfeed.ui.module.newsletter.NewsletterFragment
import javax.inject.Inject

@MainScope
class MainNavController @Inject constructor(val activity: MainActivity) {

    private val fragmentManager = activity.supportFragmentManager
    private val containerId = R.id.fragmentContainer

    private val feedFragment: FeedFragment by lazy {
        FeedFragment()
    }

    private val aboutFragment: AboutFragment by lazy {
        AboutFragment()
    }

    private val newsletterFragment: NewsletterFragment by lazy {
        NewsletterFragment()
    }

    private val contributeFragment: ContributeFragment by lazy {
        ContributeFragment()
    }

    fun openNewsFragment() {
        changeFragment(feedFragment)
    }

    fun openBookmarksFragment() {
//        changeFragment(bookmarkFragment)
    }

    fun openAboutFragment() {
        changeFragment(aboutFragment)
    }

    fun openHelpUsFragment() {
        changeFragment(contributeFragment)
    }

    fun openNewsletterFragment() {
        changeFragment(newsletterFragment)
    }

    private fun changeFragment(fragment: androidx.fragment.app.Fragment) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
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