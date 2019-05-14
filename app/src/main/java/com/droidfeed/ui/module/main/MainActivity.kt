package com.droidfeed.ui.module.main

import android.animation.ArgbEvaluator
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.databinding.ActivityMainBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelAdapter
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.ui.module.onboard.OnBoardActivity
import com.droidfeed.util.AnimUtils
import com.droidfeed.util.ColorPalette
import com.droidfeed.util.event.EventObserver
import com.droidfeed.util.extension.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class MainActivity : BaseActivity() {

    @Inject lateinit var navController: MainNavController
    @Inject lateinit var animUtils: AnimUtils
    @Inject lateinit var colorPalette: ColorPalette

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var currentMenuColor = 0
    private var previousScreenColor = 0
    private var previousMenuButton: View? = null
    private val linearLayoutManager = LinearLayoutManager(this)
    private val uiModelAdapter = UIModelAdapter(this, linearLayoutManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        setupTransparentStatusBar()
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(MainViewModel::class.java)

        subscribeUserTerms()
        subscribeNavigation()
        subscribeScrollTopEvent()
        subscribeSources()
        subscribeMenuVisibility()
        subscribeFilterVisibility()
        subscribeCloseKeyboard()
        subscribeSourceShareEvent()
        subscribeSourceRemoveUndoSnack()

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        ).apply {
            viewModel = mainViewModel
            lifecycleOwner = this@MainActivity
            appbar.containerView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        }

        initFilterDrawer()
    }

    private fun subscribeSourceShareEvent() {
        mainViewModel.shareSourceEvent.observe(this, EventObserver { content ->
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also(this::startActivity)
        })
    }

    private fun subscribeSourceRemoveUndoSnack() {
        mainViewModel.showUndoSourceRemoveSnack.observe(this, EventObserver { onUndo ->
            Snackbar.make(
                binding.root,
                R.string.info_source_removed,
                Snackbar.LENGTH_LONG
            ).apply {
                setActionTextColor(Color.YELLOW)
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
                setAction(R.string.undo) { onUndo() }
            }.run {
                show()
            }
        })
    }

    private fun subscribeCloseKeyboard() {
        mainViewModel.closeKeyboardEvent.observe(this, EventObserver {
            edtFeedUrl.hideKeyboard()
        })
    }

    private fun subscribeSources() {
        mainViewModel.sourceUIModelData.observe(this, Observer { sourceUIModels ->
            uiModelAdapter.addUIModels(sourceUIModels as List<BaseUIModelAlias>)
        })
    }

    private fun subscribeFilterVisibility() {
        mainViewModel.isSourceFilterVisible.observe(this, EventObserver { isVisible ->
            if (isVisible) {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            } else {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            }
        })
    }

    private fun subscribeNavigation() {
        mainViewModel.onNavigation.observe(this, Observer { destination ->
            if (navController.activeDestination != destination) {
                navController.open(destination)

                when (destination) {
                    Destination.FEED -> {
                        highlightSelectedMenuItem(binding.appbar.menu.btnNavHome)
                        onMenuItemSelected(colorPalette.transparent)
                        lightStatusBarTheme()
                    }
                    Destination.NEWSLETTER -> {
                        onMenuItemSelected(colorPalette.blue)
                        darkStatusBarTheme()
                        highlightSelectedMenuItem(binding.appbar.menu.btnNavNewsletter)
                    }
                    Destination.CONTRIBUTE -> {
                        highlightSelectedMenuItem(binding.appbar.menu.btnNavContribute)
                        onMenuItemSelected(colorPalette.gray)
                        darkStatusBarTheme()
                    }
                    Destination.ABOUT -> {
                        highlightSelectedMenuItem(binding.appbar.menu.btnNavAbout)
                        onMenuItemSelected(colorPalette.pink)
                        darkStatusBarTheme()
                    }
                    Destination.CONFERENCES -> {
                        highlightSelectedMenuItem(binding.appbar.menu.btnNavConferences)
                        onMenuItemSelected(colorPalette.transparent)
                        lightStatusBarTheme()
                    }
                }
            }
        })
    }

    private fun subscribeUserTerms() {
        mainViewModel.isUserTermsAccepted.observe(this, Observer { isUserTermsAccepted ->
            if (!isUserTermsAccepted) {
                startOnBoardActivity()
            }
        })
    }

    private fun subscribeScrollTopEvent() {
        mainViewModel.scrollTop.observe(this, EventObserver {
            navController.scrollToTop()
        })
    }

    private fun startOnBoardActivity() {
        Intent(
            this,
            OnBoardActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }.run {
            startActivity(this)
            overridePendingTransition(0, 0)
        }
    }

    private fun highlightSelectedMenuItem(menuItem: View) {
        menuItem.isSelected = true
        previousMenuButton?.isSelected = false
        previousMenuButton = menuItem
    }

    private fun onMenuItemSelected(color: Int) {
        binding.appbar.btnMenu.isSelected = false
        animateMenuButton()
        animateMenuColor(color)
        animateTitleColor(false)

        if (color != colorPalette.transparent) {
            animateNavigationBarColor(color)
        } else {
            animateNavigationBarColor(colorPalette.black)
        }

        currentMenuColor = color
    }

    private fun subscribeMenuVisibility() {
        mainViewModel.isMenuVisible.observe(this, Observer { isVisible ->
            if (binding.appbar.menu.containerMenu.isVisible != isVisible) {
                val color = when {
                    isVisible -> colorPalette.accent
                    else -> currentMenuColor
                }

                binding.appbar.btnMenu.run {
                    speed = if (isSelected) 1f else -1f
                    resumeAnimation()
                }

                animateMenuColor(color)
                animateTitleColor(isVisible)

                if (isVisible) {
                    binding.root.hideKeyboard()
                }
            }
        })
    }

    private fun animateMenuColor(color: Int) {
        ValueAnimator.ofArgb(
            colorPalette.accent,
            color
        ).apply {
            interpolator = animUtils.linearOutSlowInInterpolator
            duration = ANIM_DURATION
            addUpdateListener { animator ->
                window.statusBarColor = animator.animatedValue as Int
                binding.appbar.containerToolbar.setBackgroundColor(animator.animatedValue as Int)
            }
        }.run {
            start()
        }
    }

    private fun animateNavigationBarColor(color: Int) {
        ValueAnimator.ofArgb(
            previousScreenColor,
            color
        ).apply {
            interpolator = animUtils.linearOutSlowInInterpolator
            duration = ANIM_DURATION
            addUpdateListener { animator ->
                window.navigationBarColor = animator.animatedValue as Int
            }
        }.run {
            start()
        }

        previousScreenColor = color
    }

    private fun animateMenuButton() {
        binding.appbar.btnMenu.run {
            speed = if (isSelected) 1f else -1f
            resumeAnimation()
        }
    }

    private fun animateTitleColor(isActive: Boolean) {
        val isLightThemed = when (navController.activeDestination) {
            Destination.CONFERENCES,
            Destination.FEED -> true
            else -> false
        }

        val commonColor = when {
            isLightThemed -> colorPalette.grayDark
            else -> colorPalette.white
        }

        val (fromColor, toColor) = when {
            isActive -> commonColor to colorPalette.white
            else -> colorPalette.white to commonColor
        }

        ObjectAnimator.ofInt(
            binding.appbar.txtToolbarTitle,
            "textColor",
            fromColor,
            toColor
        ).apply {
            setEvaluator(ArgbEvaluator())
            interpolator = animUtils.linearOutSlowInInterpolator
            duration = ANIM_DURATION
        }.run {
            start()
        }
    }

    private fun initFilterDrawer() {
        binding.filterRecycler.apply {
            (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            adapter = uiModelAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = linearLayoutManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setFilterPaddingForCutout()
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                mainViewModel.onFilterDrawerClosed()
                drawerView.hideKeyboard()
            }

            override fun onDrawerOpened(drawerView: View) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setFilterPaddingForCutout() {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        binding.filterView.setOnApplyWindowInsetsListener { v, insets ->
            val displayCutout = insets.displayCutout
            binding.filterView.setPadding(0, displayCutout?.safeInsetTop ?: 0, 0, 0)
            insets.consumeDisplayCutout()
        }
    }

    override fun onBackPressed() {
        val isFilterDrawerOpen = binding.drawerLayout.isDrawerOpen(GravityCompat.END)

        mainViewModel.onBackPressed(isFilterDrawerOpen) {
            super.onBackPressed()
        }
    }

    companion object {
        private const val ANIM_DURATION = 300L
    }
}