package com.droidfeed.util

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import com.droidfeed.R
import com.droidfeed.data.db.PostDao
import com.droidfeed.util.glide.GlideApp
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AppRateHelper @Inject constructor(
    val sharedPrefs: SharedPreferences,
    val rssDao: PostDao
) {

    /**
     * Attempt to get app review.
     */
    internal fun checkAppRatePrompt(view: View) {
        if (sharedPrefs.appRatePrompt) {
            launch {
                val bookmarkCount = rssDao.getBookmarkedItemCount()

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
        Snackbar.make(view, R.string.do_you_like_droidfeed, 7000)
            .setAction(R.string.yes) {
                buildRateAppDialog(view.context).show()
            }
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

    private fun postponeAppRating(multiplier: Int = 1) {
        sharedPrefs.appRatePromptIndex += APP_RATE_PROMPT_INDEX * multiplier
    }

    private fun buildRateAppDialog(context: Context): AlertDialog.Builder {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_rate_app, null)

        GlideApp.with(context)
            .load(R.drawable.df_blinking)
            .into(view.findViewById(R.id.imgAppLogo))

        return AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton(R.string.sure) { _, _ ->
                context.startActivity(rateAppIntent)
            }
            .setNegativeButton(R.string.later, null)
            .setNeutralButton(R.string.never_show) { _, _ ->
                sharedPrefs.appRatePrompt = false
            }
    }
}

internal const val APP_RATE_PROMPT_INDEX = 3