package com.droidnews.ui.module.main

import com.droidnews.R
import com.droidnews.ui.module.news.NewsFragment
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
class MainNavController @Inject constructor(activity: MainActivity) {

    private val fragmentManager = activity.supportFragmentManager
    private val containerId = R.id.fragmentContainer

    val newsFragment: NewsFragment by lazy {
        NewsFragment()
    }

    fun openNewsFragment() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out)
                .replace(containerId, newsFragment)
                .commitAllowingStateLoss()
    }

    fun openBookmarksFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun openAboutFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
