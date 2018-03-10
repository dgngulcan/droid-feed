package com.droidfeed.ui.module.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.droidfeed.R
import com.droidfeed.databinding.ActivityMainBinding
import com.droidfeed.databinding.NavHeaderMainBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main_app_bar.*
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var navController: MainNavController

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var navHeaderBinding: NavHeaderMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBindings()
        init()
        initNavigationDrawer()
        initFilterDrawer()
        initObservers()
    }

    private fun init() {

        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(MainViewModel::class.java)

        setSupportActionBar(binding.appbar?.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        binding.drawerLayout.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        toolbar.getChildAt(0).setOnClickListener({
            navController.scrollToTop()
        })
    }

    private fun initBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)!!
//        binding.setLifecycleOwner(this)
        navHeaderBinding = NavHeaderMainBinding.inflate(
            layoutInflater,
            binding.navView,
            false
        )

        binding.navView.addHeaderView(navHeaderBinding.root)
    }

    private fun initNavigationDrawer() {

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appbar?.toolbar,
            0,
            0
        )

        toggle.isDrawerSlideAnimationEnabled = false
        toggle.syncState()

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                viewModel.shuffleHeaderImage()
            }

        })

        binding.navView.setNavigationItemSelectedListener(navigationListener)
        binding.navView.setCheckedItem(R.id.nav_feed)
        navController.openNewsFragment()
    }

    private fun initFilterDrawer() {

        val adapter = UiModelAdapter()
        viewModel.sourceUiModelData.observe(this, Observer {
            adapter.addUiModels(it as Collection<BaseUiModelAlias>)
        })
        (binding.filterRecycler.itemAnimator as DefaultItemAnimator)
            .supportsChangeAnimations = false
        binding.filterRecycler.adapter = adapter
        binding.filterRecycler.overScrollMode = View.OVER_SCROLL_NEVER
        binding.filterRecycler.layoutManager = LinearLayoutManager(this)
    }

    private fun initObservers() {
        viewModel.navigationHeaderImage.observe(this, Observer {
            navHeaderBinding.drawerImage = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter -> binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when {
            binding.drawerLayout.isDrawerOpen(GravityCompat.START) ->
                binding.drawerLayout.closeDrawer(GravityCompat.START)

            binding.drawerLayout.isDrawerOpen(GravityCompat.END) ->
                binding.drawerLayout.closeDrawer(GravityCompat.END)

            else -> super.onBackPressed()
        }
    }

    private val navigationListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_feed -> {
                navController.openNewsFragment()
                binding.appbar?.toolbar?.setTitle(R.string.app_name)
            }
            R.id.nav_bookmarks -> {
                navController.openBookmarksFragment()
                binding.appbar?.toolbar?.setTitle(R.string.nav_bookmarks)
            }
            R.id.nav_about -> {
                navController.openAboutFragment()
                binding.appbar?.toolbar?.setTitle(R.string.nav_about)
            }
            R.id.nav_contribute -> {
                navController.openHelpUsFragment()
                binding.appbar?.toolbar?.setTitle(R.string.nav_contribute)
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        true
    }


}
