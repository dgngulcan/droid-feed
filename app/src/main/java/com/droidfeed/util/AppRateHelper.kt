package com.droidfeed.util

import android.view.View
import androidx.core.content.ContextCompat
import com.droidfeed.R
import com.droidfeed.data.repo.SharedPrefsRepo
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class AppRateHelper @Inject constructor(
    private val sharedPrefs: SharedPrefsRepo,
    private val analyticsUtil: AnalyticsUtil
) {

    fun showRateSnackbar(view: View, onAction: () -> Unit) {
        analyticsUtil.logAppRatePrompt()

        Snackbar.make(view, R.string.like_to_review_df, 5000)
            .setAction(R.string.yes) { onAction() }
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setActionTextColor(
                ContextCompat.getColor(
                    view.context,
                    R.color.yellow
                )
            )
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    postponeAppRating()
                }
            })
            .show()
    }

    private fun postponeAppRating() {
        sharedPrefs.incrementAppRatePromptIgnoreCount()
        sharedPrefs.incrementAppRatePromptIndex()

        if (sharedPrefs.appRatePromptCount == 3) {
            sharedPrefs.setAppRatePrompt(false)
        }
    }
}
