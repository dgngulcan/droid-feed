package com.droidfeed.util

import android.graphics.ColorMatrix
import android.util.Property

/**
 * Created by Dogan Gulcan on 11/5/17.
 */
class ObservableColorMatrix : ColorMatrix() {

    var saturation = 1f
        get() = field
        set(value) {
            field = value
        }

    companion object {

        val SATURATION: Property<ObservableColorMatrix, Float> by lazy {

            object : FloatProperty<ObservableColorMatrix>("saturation") {
                override fun setValue(cm: ObservableColorMatrix, value: Float) {
                    cm.saturation = value
                }

                override operator fun get(cm: ObservableColorMatrix): Float? {
                    return cm.saturation
                }
            }
        }
    }
}