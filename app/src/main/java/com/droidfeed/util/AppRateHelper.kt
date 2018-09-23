package com.droidfeed.util

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.droidfeed.R
import com.droidfeed.data.db.PostDao
import com.droidfeed.rateAppIntent
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AppRateHelper @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val postDao: PostDao
) {

    /**
     * Attempt to get app review.
     */
    internal fun checkAppRatePrompt(view: View) {
        if (sharedPrefs.appRatePrompt) {
            GlobalScope.launch {
                val bookmarkCount = postDao.getBookmarkedItemCount()

                if (sharedPrefs.appOpenCount > sharedPrefs.appRatePromptIndex &&
                    (bookmarkCount > sharedPrefs.appRatePromptIndex ||
                            sharedPrefs.shareCount > sharedPrefs.appRatePromptIndex)
                ) {
                    showRateSnackbar(view)
                }
            }
        }
    }

    private fun showRateSnackbar(view: View) {
        Snackbar.make(view, R.string.like_to_review_df, 5000)
            .setAction(R.string.yes) { buildRateAppDialog(view.context)?.show() }
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

    private fun buildRateAppDialog(context: Context): androidx.appcompat.app.AlertDialog.Builder? {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_rate_app, null)

        return androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton(R.string.sure) { _, _ ->
                context.startActivity(rateAppIntent)
            }
            .setNegativeButton(R.string.later, null)
            .setNeutralButton(R.string.never_show) { _, _ ->
                sharedPrefs.appRatePrompt = false
            }
    }

    private fun postponeAppRating(multiplier: Int = 1) {
        sharedPrefs.appRatePromptIndex += APP_RATE_PROMPT_INDEX * multiplier
    }
}

internal const val APP_RATE_PROMPT_INDEX = 3