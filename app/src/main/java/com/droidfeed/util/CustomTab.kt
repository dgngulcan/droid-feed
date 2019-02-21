package com.droidfeed.util

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.droidfeed.R
import com.droidfeed.ui.module.webview.WebViewActivity
import com.droidfeed.util.extention.isPackageAvailable
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

/**
 * Custom Chrome Tabs helper.
 */
class CustomTab @Inject constructor(val activity: Activity) {

    private var tabClient: CustomTabsClient? = null
    private val tabIntent: CustomTabsIntent by lazy {
        CustomTabsIntent.Builder()
            .apply {
                AppCompatResources
                    .getDrawable(
                        activity,
                        R.drawable.ic_arrow_back_black_24dp
                    )
                    ?.toBitmap()
                    ?.let { setCloseButtonIcon(it) }

                setToolbarColor(
                    ContextCompat.getColor(
                        activity,
                        android.R.color.white
                    )
                )

                setStartAnimations(
                    activity,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )

                setExitAnimations(
                    activity,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            }.run {
                addDefaultShareMenuItem()
                build()
            }.also { customTabIntent ->
                customTabIntent.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
    }

    /**
     * Opens the given URL in custom tab. If custom tab service is not available, the URL will be
     * displayed on the default browser of device. If both options are not available, the URL will
     * be displayed within [WebViewActivity].
     *
     * @param url
     */
    fun showTab(url: String) {
        if (URLUtil.isValidUrl(url)) {
            when {
                isChromeCustomTabsSupported(activity) -> bindCustomTabsService(
                    url,
                    CHROME_STABLE_PACKAGE
                )
                else -> {
                    showOnDefaultBrowser(url)
                }
            }
        } else {
            Snackbar.make(
                activity.window.decorView,
                R.string.error_invalid_url,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun showOnDefaultBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val canHandleIntent = activity.isPackageAvailable(intent.`package` ?: "")

        when {
            canHandleIntent -> activity.startActivity(intent)
            else -> Intent(activity, WebViewActivity::class.java)
                .putExtra(WebViewActivity.EXTRA_URL, url)
                .also { activity.startActivity(it) }
        }
    }

    private fun bindCustomTabsService(url: String, chromePackage: String) {
        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                componentName: ComponentName,
                client: CustomTabsClient
            ) {
                tabClient = client
                launchCustomTab(url)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                tabClient = null
            }
        }

        CustomTabsClient.bindCustomTabsService(activity, chromePackage, connection)
    }

    private fun launchCustomTab(url: String) {
        tabClient?.warmup(0L)
        tabIntent.launchUrl(activity, Uri.parse(url))
    }

    private fun isChromeCustomTabsSupported(context: Context): Boolean {
        val serviceIntent = Intent(SERVICE_ACTION).apply {
            setPackage(CHROME_STABLE_PACKAGE)
        }
        val resolveInfos = context.packageManager.queryIntentServices(serviceIntent, 0)
        return !(resolveInfos == null || resolveInfos.isEmpty())
    }

    companion object {
        private const val CHROME_STABLE_PACKAGE = "com.android.chrome"
        private const val SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService"
    }
}