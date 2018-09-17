package com.droidfeed.ui.module.main

import androidx.fragment.app.Fragment
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

    private var activeFragment: Fragment? = null

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

    fun openFeedFragment() {
        changeFragment(feedFragment)
    }

    fun openAboutFragment() {
        changeFragment(aboutFragment)
    }

    fun openContributeFragment() {
        changeFragment(contributeFragment)
    }

    fun openNewsletterFragment() {
        changeFragment(newsletterFragment)
    }

    private fun changeFragment(fragment: androidx.fragment.app.Fragment) {
        activeFragment = fragment

        fragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .replace(containerId, fragment)
            .commitAllowingStateLoss()
    }

    fun isFeedFragment(): Boolean {
        return activeFragment?.equals(feedFragment) ?: false
    }

    fun scrollToTop() {
        val currentFragment = fragmentManager.findFragmentById(containerId)
        when (currentFragment) {
            is FeedFragment -> currentFragment.scrollToTop()
        }
    }
}