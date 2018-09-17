package com.droidfeed.util

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main

/**
 * A job that automatically gets cancelled when the lifecycle is destroyed. Meant to be used as a
 * parent to your coroutines in lifecycle aware components.
 *
 * @see https://gist.github.com/LouisCAD/58d3017eedb60ce00721cb32a461980f#file-lifecyclejob-kt-L30
 * @author Louis Cad
 */

fun Lifecycle.createJob(cancelEvent: Lifecycle.Event = ON_DESTROY): Job = Job().also { job ->
    addObserver(object : GenericLifecycleObserver {
        override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event) {
            if (event == cancelEvent) {
                removeObserver(this)
                job.cancel()
            }
        }
    })
}

private val lifecycleCoroutineScopes = mutableMapOf<Lifecycle, CoroutineScope>()

val Lifecycle.coroutineScope: CoroutineScope
    get() = lifecycleCoroutineScopes[this] ?: createJob().let {
        val newScope = CoroutineScope(it + Dispatchers.Main)
        lifecycleCoroutineScopes[this] = newScope
        it.invokeOnCompletion { _ -> lifecycleCoroutineScopes -= this }
        newScope
    }

val LifecycleOwner.coroutineScope get() = lifecycle.coroutineScope