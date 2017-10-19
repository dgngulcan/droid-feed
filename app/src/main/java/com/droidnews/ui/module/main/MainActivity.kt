package com.droidnews.ui.module.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.droidnews.R
import com.droidnews.databinding.ActivityMainBinding
import com.nytclient.ui.common.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var navController: MainNavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottomNavigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // default tab
        if (savedInstanceState == null) {
            binding.bottomNavigationBar.selectedItemId = R.id.nav_news  /* default tab */
        }

        init()
    }

    private fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_news -> {
                navController.openNewsFragment()
                true
            }
            R.id.nav_bookmarks -> {
                navController.openBookmarksFragment()
                true
            }
            R.id.nav_about -> {
                navController.openAboutFragment()
                true
            }
            else -> false
        }
    }

}
