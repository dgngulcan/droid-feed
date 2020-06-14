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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidfeed.R
import com.droidfeed.data.repo.SharedPrefsRepo
import com.droidfeed.databinding.ActivityMainBinding
import com.droidfeed.ui.adapter.BaseUIModelAlias
import com.droidfeed.ui.adapter.UIModelAdapter
import com.droidfeed.ui.common.BaseActivity
import com.droidfeed.ui.module.onboard.OnBoardActivity
import com.droidfeed.util.AnimUtils
import com.droidfeed.util.ColorPalette
import com.droidfeed.util.extension.addOnDrawerClosedListener
import com.droidfeed.util.extension.hideKeyboard
import com.droidfeed.util.extension.observeEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject lateinit var animUtils: AnimUtils
    @Inject lateinit var colorPalette: ColorPalette
    @Inject lateinit var sharedPrefs: SharedPrefsRepo
    @Inject lateinit var uiModelAdapter: UIModelAdapter
    @Inject lateinit var navController: MainNavController

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var currentMenuColor = 0
    private var previousScreenColor = 0
    private var previousMenuButton: View? = null
    private val linearLayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        ).apply {
            viewModel = mainViewModel
            lifecycleOwner = this@MainActivity
            appbar.containerView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        }

        if (!sharedPrefs.hasAcceptedTerms) {
            startOnBoardActivity()
        }

        subscribeNavigation()
        subscribeScrollTopEvent()
        subscribeSources()
        subscribeMenuVisibility()
        subscribeFilterVisibility()
        subscribeCloseKeyboard()
        subscribeSourceShareEvent()
        subscribeSourceRemoveUndoSnack()

        initFilterDrawer()
    }

    private fun subscribeSourceShareEvent() {
        mainViewModel.shareSourceEvent.observeEvent(this) { content ->
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }.also(::startActivity)
        }
    }

    private fun subscribeSourceRemoveUndoSnack() {
        mainViewModel.showUndoSourceRemoveSnack.observeEvent(this) { onUndo ->
            Snackbar.make(
                binding.root,
                R.string.info_source_removed,
                Snackbar.LENGTH_LONG
            ).apply {
                setActionTextColor(Color.YELLOW)
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
                setAction(R.string.undo) { onUndo() }
            }.also {
                it.show()
            }
        }
    }

    private fun subscribeCloseKeyboard() {
        mainViewModel.closeKeyboardEvent.observe(this) {
            edtFeedUrl.hideKeyboard()
        }
    }

    private fun subscribeSources() {
        mainViewModel.sourceUIModelData.observe(this) { sourceUIModels ->
            uiModelAdapter.addUIModels(sourceUIModels as List<BaseUIModelAlias>)
        }
    }

    private fun subscribeFilterVisibility() {
        mainViewModel.isSourceFilterVisible.observeEvent(this) { isVisible ->
            when {
                isVisible -> binding.drawerLayout.openDrawer(GravityCompat.END)
                else -> binding.drawerLayout.closeDrawer(GravityCompat.END)
            }
        }
    }

    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    private fun subscribeNavigation() {
        mainViewModel.onNavigation.observe(this) { destination ->
            if (navController.activeDestination != destination) {
                navController.open(destination)
                updateUiForDestination(destination)
            }
        }
    }

    private fun updateUiForDestination(destination: Destination?) {
        when (destination) {
            Destination.FEED -> {
                highlightSelectedMenuItem(binding.appbar.menu.btnNavHome)
                onMenuItemSelected(colorPalette.transparent)
                lightStatusBarTheme()
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

    private fun subscribeScrollTopEvent() {
        mainViewModel.scrollTop.observeEvent(this) {
            navController.scrollToTop()
        }
    }

    private fun startOnBoardActivity() {
        Intent(
            this,
            OnBoardActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }.also {
            startActivity(it)
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
                    speed = if (isVisible) 1f else -1f
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

        binding.drawerLayout.addOnDrawerClosedListener { drawerView ->
            mainViewModel.onFilterDrawerClosed()
            drawerView.hideKeyboard()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setFilterPaddingForCutout() {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        binding.filterView.setOnApplyWindowInsetsListener { _, insets ->
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