package com.droidfeed.ui.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Extensions to provide [CoroutineScope] for [LifecycleOwner]s.
 *
 * Adopted from https://gist.github.com/LouisCAD/58d3017eedb60ce00721cb32a461980f
 */

fun Lifecycle.createJob(cancelEvent: Lifecycle.Event = ON_DESTROY): Job {
    if (cancelEvent in forbiddenCancelEvents) {
        throw UnsupportedOperationException("$cancelEvent is forbidden for createJob(…).")
    }
    return Job().also { job ->
        if (currentState == Lifecycle.State.DESTROYED) job.cancel()
        else addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == cancelEvent) {
                    removeObserver(this)
                    job.cancel()
                }
            }
        })
    }
}

private val forbiddenCancelEvents = arrayOf(
    Lifecycle.Event.ON_ANY,
    Lifecycle.Event.ON_CREATE,
    Lifecycle.Event.ON_START,
    Lifecycle.Event.ON_RESUME
)
private val lifecycleJobs = mutableMapOf<Lifecycle, Job>()

val Lifecycle.job: Job
    get() = lifecycleJobs[this] ?: createJob().also { job ->
        if (job.isActive) {
            lifecycleJobs[this] = job
            job.invokeOnCompletion { lifecycleJobs -= this }
        }
    }
private val lifecycleCoroutineScopes = mutableMapOf<Lifecycle, CoroutineScope>()

val Lifecycle.coroutineScope: CoroutineScope
    get() = lifecycleCoroutineScopes[this] ?: job.let { job ->
        val newScope = CoroutineScope(job + Dispatchers.Main)
        if (job.isActive) {
            lifecycleCoroutineScopes[this] = newScope
            job.invokeOnCompletion { lifecycleCoroutineScopes -= this }
        }
        newScope
    }

inline val LifecycleOwner.coroutineScope get() = lifecycle.coroutineScope

fun Lifecycle.createScope(cancelEvent: Lifecycle.Event): CoroutineScope {
    return CoroutineScope(createJob(cancelEvent) + Dispatchers.Main)
}
