package com.droidfeed.util.extention

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.res.Resources

/**
 * Created by Dogan Gulcan on 11/8/17.
 */

val Int.asPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.asDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

inline fun <T> LiveData<T>.reObserve(owner: LifecycleOwner,
                                     crossinline func: (T?) ->
                                     (Unit)) {
    removeObservers(owner)
    observe(owner, Observer<T> { t -> func(t) })
}

inline fun <T> LiveData<T>.reObserve(owner: LifecycleOwner,
                                     observer: Observer<T>) {
    removeObservers(owner)
    observe(owner, observer)
}