package com.droidfeed.ui.module.main

import android.animation.ArgbEvaluator
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.droidfeed.R
import com.droidfeed.databinding.ActivityMainBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_main_app_bar.view.*
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class MainActivity : BaseActivity() {

    @Inject
    lateinit var navController: MainNavController

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private val transparentColor by lazy {
        ContextCompat.getColor(
                this,
                android.R.color.transparent
        )
    }
    private val accentColor by lazy { ContextCompat.getColor(this, R.color.colorAccent) }

    private val menuTransDrawable by lazy {
        TransitionDrawable(
                arrayOf(
                        ColorDrawable(transparentColor),
                        ColorDrawable(accentColor)
                )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isMarshmallow()) {
            setupTransparentStatusbar()
            lightStatusbarTheme()
        }
        super.onCreate(savedInstanceState)
        initBindings()
        init()
        initFilterDrawer()
    }

    private fun initBindings() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private fun init() {
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MainViewModel::class.java)

        binding.appbar.btnMenu.setOnClickListener {
            animateMenu(it)
        }

        // fixes the glitch when opening menu with animateLayoutChanges
        val layoutTransition = binding.appbar.containerView.layoutTransition
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.appbar.containerToolbar.background = menuTransDrawable
        navController.openNewsFragment()

        binding.appbar.containerToolbar.btnFilter.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.appbar.containerToolbar.btnBookmarks.setOnClickListener {
            it.isSelected = !it.isSelected

            viewModel.onBookmarksEvent(it.isSelected)

            binding.appbar.containerToolbar.btnFilter.visibility =
                    if (it.isSelected) View.GONE else View.VISIBLE
        }

    }

    private fun animateMenu(it: View) {
        it.isSelected = !it.isSelected
        animateMenuButton(it)
        animateTitleColor(it.isSelected)
        animateMenuColor(it.isSelected)

        binding.appbar.menu.visibility = if (it.isSelected) View.VISIBLE else View.GONE
    }

    private fun animateMenuColor(selected: Boolean) {
        val valueAnimator = if (selected) {
            ValueAnimator.ofArgb(transparentColor, accentColor)
        } else {
            ValueAnimator.ofArgb(accentColor, transparentColor)
        }
        valueAnimator.interpolator = LinearOutSlowInInterpolator()
        valueAnimator.duration = 400
        valueAnimator.addUpdateListener { valueAnimator ->
            window.statusBarColor = valueAnimator.animatedValue as Int
            binding.appbar.containerToolbar.setBackgroundColor(valueAnimator.animatedValue as Int)

        }
        valueAnimator.start()
    }

    private fun animateMenuButton(it: View) {
        if (it.isSelected) {
            binding.appbar.btnMenu.speed = 1f
            binding.appbar.btnMenu.resumeAnimation()
        } else {
            binding.appbar.btnMenu.speed = -1f
            binding.appbar.btnMenu.resumeAnimation()
        }

        val optionsMenuVisibility = if (it.isSelected) View.GONE else View.VISIBLE
        binding.appbar.containerToolbar.btnFilter.visibility = optionsMenuVisibility
        binding.appbar.containerToolbar.btnBookmarks.visibility = optionsMenuVisibility
    }

    private fun animateTitleColor(active: Boolean) {
        val fromColor = if (active) {
            ContextCompat.getColor(this, R.color.grayDark1)
        } else {
            ContextCompat.getColor(this, android.R.color.white)
        }

        val toColor = if (active) {
            ContextCompat.getColor(this, android.R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.grayDark1)
        }

        ObjectAnimator.ofInt(
                binding.appbar.txtTitle,
                "textColor",
                fromColor,
                toColor
        ).apply {
            setEvaluator(ArgbEvaluator())
            interpolator = LinearOutSlowInInterpolator()
            duration = 300
        }.also { it.start() }
    }

    private fun initFilterDrawer() {
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val adapter = UiModelAdapter(layoutManager = layoutManager)

        viewModel.sourceUiModelData.observe(this, Observer {
            adapter.addUiModels(it as Collection<BaseUiModelAlias>)
        })

        (binding.filterRecycler.itemAnimator as androidx.recyclerview.widget.DefaultItemAnimator)
                .supportsChangeAnimations = false
        binding.filterRecycler.adapter = adapter
        binding.filterRecycler.overScrollMode = View.OVER_SCROLL_NEVER
        binding.filterRecycler.layoutManager = layoutManager
    }

    override fun onBackPressed() {
        when {
            binding.drawerLayout.isDrawerOpen(GravityCompat.END) ->
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            else -> super.onBackPressed()
        }
    }

//    private val navigationListener = NavigationView.OnNavigationItemSelectedListener { item ->
//        when (item.itemId) {
//            R.id.nav_feed -> {
//                navController.openNewsFragment()
//                binding.appbar?.toolbar?.setTitle(R.string.app_name)
//            }
//            R.id.nav_bookmarks -> {
//                navController.openBookmarksFragment()
//                binding.appbar?.toolbar?.setTitle(R.string.nav_bookmarks)
//            }
//            R.id.nav_about -> {
//                navController.openAboutFragment()
//                binding.appbar?.toolbar?.setTitle(R.string.nav_about)
//            }
//            R.id.nav_newsletter -> {
//                navController.openNewsletterFragment()
//                binding.appbar?.toolbar?.setTitle(R.string.nav_newsletter)
//            }
//            R.id.nav_contribute -> {
//                navController.openHelpUsFragment()
//                binding.appbar?.toolbar?.setTitle(R.string.nav_contribute)
//            }
//        }
//
//        binding.drawerLayout.closeDrawer(GravityCompat.START)
//        true
//    }
}