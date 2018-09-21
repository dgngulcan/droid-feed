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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.databinding.ActivityMainBinding
import com.droidfeed.ui.adapter.BaseUiModelAlias
import com.droidfeed.ui.adapter.UiModelAdapter
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.util.AnimUtils
import com.droidfeed.util.event.EventObserver
import com.droidfeed.util.isMarshmallow
import kotlinx.android.synthetic.main.activity_main_app_bar.view.*
import kotlinx.android.synthetic.main.menu_main.view.*
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class MainActivity : BaseActivity() {

    @Inject
    lateinit var navController: MainNavController

    @Inject
    lateinit var animUtils: AnimUtils

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private val transColor by lazy { ContextCompat.getColor(this, R.color.transparent) }
    private val accentColor by lazy { ContextCompat.getColor(this, R.color.colorAccent) }
    private val whiteColor by lazy { ContextCompat.getColor(this, android.R.color.white) }
    private val blackColor by lazy { ContextCompat.getColor(this, android.R.color.black) }
    private val pinkColor by lazy { ContextCompat.getColor(this, R.color.pink) }
    private val blueColor by lazy { ContextCompat.getColor(this, R.color.blue) }
    private val grayColor by lazy { ContextCompat.getColor(this, R.color.gray) }
    private val grayDarkColor by lazy { ContextCompat.getColor(this, R.color.grayDark1) }

    private var currentMenuColor = 0
    private var previousScreenColor = 0
    private var previousMenuButton: View? = null

    private val menuTransDrawable by lazy {
        TransitionDrawable(
            arrayOf(
                ColorDrawable(transColor),
                ColorDrawable(accentColor)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isMarshmallow()) {
            setupTransparentStatusBar()
            lightStatusBarTheme()
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

        currentMenuColor = transColor

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

        viewModel.hideMenuEvent.observe(this, EventObserver {
            if (binding.appbar.btnMenu.isSelected) {
                animateMenu(binding.appbar.btnMenu)
            }
        })

        initNavigationClicks()
    }

    private fun initNavigationClicks() {
        binding.appbar.containerToolbar.btnNavHome.setOnClickListener {
            highlightSelectedMenuButton(it)
            navController.openFeedFragment()
            binding.appbar.txtTitle.text = getString(R.string.app_name_lower)
            onMenuItemSelected(transColor)
            toggleFilterMenu(true)
            toggleBookmarksMenu(true)
            if (isMarshmallow()) {
                lightStatusBarTheme()
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
                darkStatusBarTheme()
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
                darkStatusBarTheme()
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
                darkStatusBarTheme()
            }
        }

        // open home screen initially
        binding.appbar.containerToolbar.btnNavHome.performClick()
    }

    private fun highlightSelectedMenuButton(it: View?) {
        it?.isSelected = true
        previousMenuButton?.isSelected = false
        previousMenuButton = it
    }

    private fun onMenuItemSelected(color: Int) {
        toggleMenu(false)
        binding.appbar.btnMenu.isSelected = false
        animateMenuButton(binding.appbar.btnMenu)
        animateTitleColor(binding.appbar.btnMenu.isSelected)
        currentMenuColor = color
        animateMenuColor(color)

        if (color != transColor) {
            animateNavigationBarColor(color)
        } else {
            animateNavigationBarColor(blackColor)
        }
    }

    private fun toggleFilterMenu(show: Boolean) {
        binding.appbar.containerToolbar.btnFilter.visibility = if (show) {
            when {
                !binding.appbar.containerToolbar.btnBookmarks.isSelected -> View.VISIBLE
                else -> View.GONE
            }
        } else {
            View.GONE
        }
    }

    private fun toggleBookmarksMenu(show: Boolean) {
        binding.appbar.containerToolbar.btnBookmarks.visibility =
                if (show) View.VISIBLE else View.GONE
    }

    private fun animateMenu(it: View) {
        it.isSelected = !it.isSelected

        val color = when {
            it.isSelected -> accentColor
            else -> currentMenuColor
        }

        animateMenuColor(color)
        animateMenuButton(it)
        animateTitleColor(it.isSelected)
        toggleMenu(it.isSelected)
    }

    private fun toggleMenu(showMenu: Boolean) {
        binding.appbar.menu.visibility = if (showMenu) View.VISIBLE else View.GONE
        toggleFilterMenu(!showMenu && navController.isFeedFragment())
        toggleBookmarksMenu(!showMenu && navController.isFeedFragment())
    }

    private fun animateMenuColor(color: Int) {
        val valueAnimator = ValueAnimator.ofArgb(accentColor, color)
        var animatorColor: Int

        valueAnimator.apply {
            interpolator = animUtils.linearOutSlowInInterpolator
            duration = ANIM_DURATION
            addUpdateListener { animator ->
                animatorColor = animator.animatedValue as Int
                window.statusBarColor = animatorColor
                binding.appbar.containerToolbar.setBackgroundColor(animatorColor)
            }
        }

        valueAnimator.start()
    }

    private fun animateNavigationBarColor(color: Int) {
        val valueAnimator = ValueAnimator.ofArgb(previousScreenColor, color)
        var animatorColor: Int

        valueAnimator.apply {
            interpolator = animUtils.linearOutSlowInInterpolator
            duration = ANIM_DURATION
            addUpdateListener { animator ->
                animatorColor = animator.animatedValue as Int
                window.navigationBarColor = animatorColor
            }
        }

        previousScreenColor = color

        valueAnimator.start()
    }

    private fun animateMenuButton(it: View) {
        binding.appbar.btnMenu.speed = if (it.isSelected) 1f else -1f
        binding.appbar.btnMenu.resumeAnimation()
    }

    private fun animateTitleColor(active: Boolean) {
        val isFeedFragmentActive = navController.isFeedFragment()

        val commonColor = when {
            isFeedFragmentActive -> grayDarkColor
            else -> whiteColor
        }

        val fromColor = when {
            active -> commonColor
            else -> whiteColor
        }

        val toColor = when {
            active -> whiteColor
            else -> commonColor
        }

        ObjectAnimator.ofInt(
            binding.appbar.txtTitle,
            "textColor",
            fromColor,
            toColor
        ).apply {
            setEvaluator(ArgbEvaluator())
            interpolator = animUtils.linearOutSlowInInterpolator
            duration = ANIM_DURATION
        }.also { it.start() }
    }

    private fun initFilterDrawer() {
        val linearLayoutManager = LinearLayoutManager(this)
        val uiModelAdapter = UiModelAdapter(layoutManager = linearLayoutManager)

        viewModel.sourceUiModelData.observe(this, Observer {
            uiModelAdapter.addUiModels(it as Collection<BaseUiModelAlias>)
        })

        binding.filterRecycler.apply {
            (itemAnimator as androidx.recyclerview.widget.DefaultItemAnimator)
                .supportsChangeAnimations = false

            adapter = uiModelAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = linearLayoutManager
        }
    }

    override fun onBackPressed() {
        when {
            binding.drawerLayout.isDrawerOpen(GravityCompat.END) -> binding.drawerLayout.closeDrawer(
                GravityCompat.END
            )
            binding.appbar.btnMenu.isSelected -> animateMenu(binding.appbar.btnMenu)
            else -> super.onBackPressed()
        }
    }

    companion object {
        private const val ANIM_DURATION = 300L
    }
}