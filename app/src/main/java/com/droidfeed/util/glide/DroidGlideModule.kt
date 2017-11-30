package com.droidfeed.util.glide

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Dogan Gulcan on 11/8/17.
 */
@GlideModule
class DroidGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager

        builder.setDefaultRequestOptions(
                RequestOptions()
                        .format(
                                if (activityManager.isLowRamDevice) {
                                    DecodeFormat.PREFER_RGB_565
                                } else {
                                    DecodeFormat.PREFER_ARGB_8888
                                }
                        ).disallowHardwareConfig())
    }

    override fun isManifestParsingEnabled(): Boolean = false

}