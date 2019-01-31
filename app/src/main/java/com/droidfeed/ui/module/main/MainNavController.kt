package com.droidfeed.ui.module.main

import androidx.fragment.app.Fragment
import com.droidfeed.R
import com.droidfeed.di.MainScope
import com.droidfeed.ui.common.Scrollable
import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.contribute.ContributeFragment
import com.droidfeed.ui.module.feed.FeedFragment
import com.droidfeed.ui.module.newsletter.NewsletterFragment
import javax.inject.Inject

@MainScope
class MainNavController @Inject constructor(val activity: MainActivity) {

    private val fragmentManager = activity.supportFragmentManager
    private val containerId = R.id.fragmentContainer

    var activeDestination: Destination? = null
        private set

    private val feedFragment: FeedFragment by lazy { FeedFragment() }
    private val aboutFragment: AboutFragment by lazy { AboutFragment() }
    private val newsletterFragment: NewsletterFragment by lazy { NewsletterFragment() }
    private val contributeFragment: ContributeFragment by lazy { ContributeFragment() }

    private fun changeFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .replace(containerId, fragment)
            .commit()
    }

    fun scrollToTop() {
        val currentFragment = fragmentManager.findFragmentById(containerId)

        if (currentFragment is Scrollable) {
            currentFragment.scrollToTop()
        }
    }

    fun open(destination: Destination) {
        if (activeDestination != destination) {
            activeDestination = destination

            when (destination) {
                Destination.FEED -> changeFragment(feedFragment)
                Destination.ABOUT -> changeFragment(aboutFragment)
                Destination.NEWSLETTER -> changeFragment(newsletterFragment)
                Destination.CONTRIBUTE -> changeFragment(contributeFragment)
            }
        }
    }
}