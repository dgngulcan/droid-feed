package com.droidfeed.util.extension

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.droidfeed.util.event.Event
import com.droidfeed.util.event.EventObserver


/**
 *  Also see [androidx.lifecycle.observe]
 */
@MainThread
inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Observer<T> {
    val wrappedObserver = EventObserver<T> { t -> onChanged.invoke(t) }
    observe(owner, wrappedObserver)
    return wrappedObserver as Observer<T>
}

fun <T> MutableLiveData<Event<T>>.postEvent(value: T?) {
    postValue(Event(value))
}