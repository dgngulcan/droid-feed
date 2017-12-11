package com.droidfeed.util

import android.content.ComponentName
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsServiceConnection
import android.support.v4.content.ContextCompat
import android.view.View
import android.webkit.URLUtil
import com.droidfeed.App
import com.droidfeed.R
import org.jetbrains.anko.design.snackbar
import javax.inject.Inject


/**
 * Created by Dogan Gulcan on 11/8/17.
 */
class CustomTab @Inject constructor(val app: App) {

    fun showTab(url: String) {

        if (URLUtil.isValidUrl(url)) {

            val connection = object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(componentName: ComponentName,
                                                          client: CustomTabsClient) {
                    client.warmup(0L)
                    val builder = CustomTabsIntent.Builder()
                    builder.setToolbarColor(ContextCompat.getColor(app, R.color.colorPrimary))

                    builder.setStartAnimations(app,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    builder.setExitAnimations(app,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)

                    val customTabsIntent = builder.build()

                    customTabsIntent.launchUrl(app, Uri.parse(url))
                }

                override fun onServiceDisconnected(name: ComponentName) {}
            }

            CustomTabsClient.bindCustomTabsService(app, "com.android.chrome", connection)

        } else {
            snackbar(View(app), app.getString(R.string.error_invalid_article_url))
        }

    }
}