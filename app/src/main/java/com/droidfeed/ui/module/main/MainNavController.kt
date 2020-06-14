package com.droidfeed.ui.module.main

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.droidfeed.R
import com.droidfeed.ui.common.Scrollable
import com.droidfeed.ui.module.about.AboutFragment
import com.droidfeed.ui.module.conferences.ConferencesFragment
import com.droidfeed.ui.module.contribute.ContributeFragment
import com.droidfeed.ui.module.feed.FeedFragment
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class MainNavController @Inject constructor(@ActivityContext val context: Context) {

    private val fragmentManager = (context as AppCompatActivity).supportFragmentManager
    private val containerId = R.id.fragmentContainer

    var activeDestination: Destination? = null
        private set

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
                Destination.FEED -> changeFragment(FeedFragment())
                Destination.ABOUT -> changeFragment(AboutFragment())
                Destination.CONTRIBUTE -> changeFragment(ContributeFragment())
                Destination.CONFERENCES -> changeFragment(ConferencesFragment())
            }
        }
    }
}