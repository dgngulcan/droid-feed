package com.droidfeed.util

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.webkit.URLUtil
import com.droidfeed.R
import com.droidfeed.util.extention.isOnline
import javax.inject.Inject


/**
 * Created by Dogan Gulcan on 11/8/17.
 */
class CustomTab @Inject constructor(val activity: Activity) {

    fun showTab(url: String) {
        if (activity.isOnline()) {
            if (URLUtil.isValidUrl(url)) {
                val chromePackage = "com.android.chrome"
                if (isPackageExists(chromePackage)) {
                    val connection = object : CustomTabsServiceConnection() {
                        override fun onCustomTabsServiceConnected(
                            componentName: ComponentName,
                            client: CustomTabsClient
                        ) {
                            launchCustomTab(client, url)
                        }

                        override fun onServiceDisconnected(name: ComponentName) {
                        }
                    }

                    CustomTabsClient.bindCustomTabsService(activity, chromePackage, connection)

                } else {
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(activity, Uri.parse(url))

                }

            } else {
                Snackbar.make(
                    activity.window.decorView.rootView,
                    R.string.error_invalid_article_url,
                    Snackbar.LENGTH_LONG
                ).show()
            }

        } else {
            Snackbar.make(
                activity.window.decorView.rootView,
                R.string.info_no_internet,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Launches a customized tab with animation.
     */
    private fun launchCustomTab(
        client: CustomTabsClient,
        url: String
    ) {
        client.warmup(0L)
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(
            ContextCompat.getColor(
                activity,
                R.color.colorPrimary
            )
        )

        builder.setStartAnimations(
            activity,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        builder.setExitAnimations(
            activity,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        val customTabsIntent = builder.build()

        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }

    private fun isPackageExists(targetPackage: String): Boolean {
        val pm = activity.packageManager
        try {
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

}