package com.droidfeed.util

import com.droidfeed.App
import com.droidfeed.util.extention.isOnline
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by Dogan Gulcan on 12/11/17.
 */
@Singleton
class NetworkUtils @Inject constructor(private val appContext: App) {

    fun isDeviceConnectedToInternet(): Boolean {
        return appContext.isOnline()
    }
}
