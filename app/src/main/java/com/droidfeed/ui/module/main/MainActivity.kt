package com.droidfeed.ui.module.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import com.droidfeed.R
import com.droidfeed.databinding.ActivityMainBinding
import com.droidfeed.databinding.NavHeaderMainBinding
import com.nytclient.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject lateinit var navController: MainNavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var navHeaderBinding: NavHeaderMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBindings()
        init()
        initDrawer()
        initObservers()
    }

    private fun init() {

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setSupportActionBar(binding.appbar.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        binding.drawerLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    private fun initBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navHeaderBinding = NavHeaderMainBinding.inflate(
                layoutInflater,
                binding.navView,
                false)

        binding.navView.addHeaderView(navHeaderBinding.root)
    }


    private fun initDrawer() {
        val toggle = ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.appbar.toolbar,
                0,
                0)

        toggle.isDrawerSlideAnimationEnabled = false
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(navigationListener)
        navView.setCheckedItem(R.id.nav_feed)
        navController.openNewsFragment()
    }

    private fun initObservers() {
        viewModel.navigationHeaderImage.observe(this, Observer {
            //            navHeaderBinding.drawerImage = it
        })
    }

    // todo move to vm
    private val navigationListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_feed -> navController.openNewsFragment()
            R.id.nav_bookmarks -> navController.openBookmarksFragment()
            R.id.nav_about -> navController.openAboutFragment()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        true
    }

}
