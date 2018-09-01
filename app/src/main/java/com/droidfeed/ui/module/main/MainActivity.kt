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
import kotlinx.android.synthetic.main.menu_main.view.*
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
    private val pinkColor by lazy { ContextCompat.getColor(this, R.color.pink) }
    private val blueColor by lazy { ContextCompat.getColor(this, R.color.blue) }
    private val grayColor by lazy { ContextCompat.getColor(this, R.color.gray) }

    private var currentMenuColor = 0
    private var previousMenuColor = 0
    private var previousMenuButton: View? = null

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

        currentMenuColor = transparentColor

        // fixes the glitch when opening menu with animateLayoutChanges
        val layoutTransition = binding.appbar.containerView.layoutTransition
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.appbar.containerToolbar.background = menuTransDrawable

        binding.appbar.containerToolbar.btnFilter.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.appbar.containerToolbar.btnBookmarks.setOnClickListener {
            it.isSelected = !it.isSelected
            viewModel.onBookmarksEvent(it.isSelected)
            toggleFilterMenu(!it.isSelected)
        }

        initNavigationClicks()
    }

    private fun initNavigationClicks() {
        binding.appbar.containerToolbar.btnNavHome.setOnClickListener {
            highlightSelectedMenuButton(it)
            navController.openFeedFragment()
            binding.appbar.txtTitle.text = getString(R.string.app_name)
            onMenuItemSelected(transparentColor)
            toggleFilterMenu(true)
            toggleBookmarksMenu(true)
            if (isMarshmallow()) {
                lightStatusbarTheme()
            }
        }

        binding.appbar.containerToolbar.btnNavNewsletter.setOnClickListener {
            highlightSelectedMenuButton(it)
            navController.openNewsletterFragment()
            binding.appbar.txtTitle.text = getString(R.string.nav_newsletter)
            onMenuItemSelected(blueColor)
            toggleFilterMenu(false)
            toggleBookmarksMenu(false)
            if (isMarshmallow()) {
                darkStatusbarTheme()
            }
        }

        binding.appbar.containerToolbar.btnNavContribute.setOnClickListener {
            highlightSelectedMenuButton(it)
            navController.openContributeFragment()
            binding.appbar.txtTitle.text = getString(R.string.nav_contribute)
            onMenuItemSelected(grayColor)
            toggleFilterMenu(false)
            toggleBookmarksMenu(false)
            if (isMarshmallow()) {
                darkStatusbarTheme()
            }
        }

        binding.appbar.containerToolbar.btnNavAbout.setOnClickListener {
            highlightSelectedMenuButton(it)
            navController.openAboutFragment()
            binding.appbar.txtTitle.text = getString(R.string.nav_about)
            onMenuItemSelected(pinkColor)
            toggleFilterMenu(false)
            toggleBookmarksMenu(false)
            if (isMarshmallow()) {
                darkStatusbarTheme()
            }
        }

        binding.appbar.containerToolbar.btnNavHome.performClick()
    }

    private fun highlightSelectedMenuButton(it: View?) {
        it?.isActivated = true
        previousMenuButton?.isActivated = false
        previousMenuButton = it
    }

    private fun onMenuItemSelected(color: Int) {
        toggleMenu(false)
        binding.appbar.btnMenu.isSelected = false
        animateMenuButton(binding.appbar.btnMenu)
        animateTitleColor(binding.appbar.btnMenu.isSelected)
        currentMenuColor = color
        animateMenuColor(color)
    }

    private fun toggleFilterMenu(show: Boolean) {
        binding.appbar.containerToolbar.btnFilter.visibility = if (show) {
            if (!binding.appbar.containerToolbar.btnBookmarks.isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }
        } else {
            View.GONE
        }
    }

    private fun toggleBookmarksMenu(show: Boolean) {
        binding.appbar.containerToolbar.btnBookmarks.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun animateMenu(it: View) {
        it.isSelected = !it.isSelected

        val color = if (it.isSelected) {
            accentColor
        } else {
            currentMenuColor
        }

        animateMenuColor(color)
        animateMenuButton(it)
        animateTitleColor(it.isSelected)
        toggleMenu(it.isSelected)
    }

    private fun toggleMenu(showMenu: Boolean) {
        binding.appbar.menu.visibility = if (showMenu) View.VISIBLE else View.GONE

        if (showMenu && navController.isFeedFragment()) {
            toggleFilterMenu(!showMenu)
            toggleBookmarksMenu(!showMenu)
        }
    }

    private fun animateMenuColor(color: Int) {
        val valueAnimator = ValueAnimator.ofArgb(previousMenuColor, color)
        valueAnimator.interpolator = LinearOutSlowInInterpolator()
        valueAnimator.duration = 300
        valueAnimator.addUpdateListener { animator ->
            window.statusBarColor = animator.animatedValue as Int
            binding.appbar.containerToolbar.setBackgroundColor(animator.animatedValue as Int)
        }

        previousMenuColor = currentMenuColor

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

//        toggleFilterMenu(!it.isSelected)
//        toggleBookmarksMenu(!it.isSelected)
    }

    private fun animateTitleColor(active: Boolean) {
        val isFeedFragmentActive = navController.isFeedFragment()

        val fromColor = if (active) {
            if (isFeedFragmentActive) {
                ContextCompat.getColor(this, R.color.grayDark1)
            } else {
                ContextCompat.getColor(this, android.R.color.white)
            }
        } else {
            ContextCompat.getColor(this, android.R.color.white)
        }

        val toColor = if (active) {
            ContextCompat.getColor(this, android.R.color.white)
        } else {
            if (isFeedFragmentActive) {
                ContextCompat.getColor(this, R.color.grayDark1)
            } else {
                ContextCompat.getColor(this, android.R.color.white)
            }
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
            binding.appbar.btnMenu.isSelected -> animateMenu(binding.appbar.btnMenu)

            else -> super.onBackPressed()
        }
    }
}