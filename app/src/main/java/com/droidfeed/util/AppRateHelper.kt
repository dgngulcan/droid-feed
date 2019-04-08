package com.droidfeed.util

import android.content.SharedPreferences
import android.view.View
import androidx.core.content.ContextCompat
import com.droidfeed.R
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class AppRateHelper @Inject constructor(private val sharedPrefs: SharedPreferences) {

    fun showRateSnackbar(view: View, onAction: () -> Unit) {
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
        sharedPrefs.appRatePromptIgnoreCount += 1
        sharedPrefs.appRatePromptIndex += APP_RATE_PROMPT_INDEX * 3

        if (sharedPrefs.appRatePromptIgnoreCount == 3) {
            sharedPrefs.appRatePrompt = false
        }
    }
}
