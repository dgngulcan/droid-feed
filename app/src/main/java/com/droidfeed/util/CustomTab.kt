package com.droidfeed.util

import android.app.Activity
import android.content.ComponentName
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.v4.content.ContextCompat
import android.view.View
import android.webkit.URLUtil
import com.droidfeed.R
import org.jetbrains.anko.design.snackbar
import javax.inject.Inject


/**
 * Created by Dogan Gulcan on 11/8/17.
 */
class CustomTab @Inject constructor(val activity: Activity) {

    fun showTab(url: String) {

        if (URLUtil.isValidUrl(url)) {

            val connection = object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(componentName: ComponentName,
                                                          client: CustomTabsClient) {
                    client.warmup(0L)
                    val builder = CustomTabsIntent.Builder()
                    builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))

                    builder.setStartAnimations(activity,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    builder.setExitAnimations(activity,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)

                    val customTabsIntent = builder.build()

                    customTabsIntent.launchUrl(activity, Uri.parse(url))
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            }

            CustomTabsClient.bindCustomTabsService(activity, "com.android.chrome", connection)

        } else {
            snackbar(View(activity), activity.getString(R.string.error_invalid_article_url))
        }

    }
}