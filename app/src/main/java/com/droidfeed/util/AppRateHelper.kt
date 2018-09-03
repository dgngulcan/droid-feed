package com.droidfeed.util

import android.content.SharedPreferences
import android.view.View
import androidx.core.content.ContextCompat
import com.droidfeed.R
import com.droidfeed.data.db.PostDao
import com.google.android.material.snackbar.Snackbar
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
            launch {
                val bookmarkCount = postDao.getBookmarkedItemCount()

                if (sharedPrefs.appOpenCount > sharedPrefs.appRatePromptIndex &&
                    (bookmarkCount > sharedPrefs.appRatePromptIndex ||
                            sharedPrefs.shareCount > sharedPrefs.appRatePromptIndex)
                ) {

//                    showRateSnackbar(view)
                }
            }
        }
    }

    private fun showRateSnackbar(view: View) {
        Snackbar.make(view, R.string.do_you_like_droidfeed, 7000)
            .setAction(R.string.yes) {
                //                buildRateAppDialog(view.context).show()
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

}

internal const val APP_RATE_PROMPT_INDEX = 3