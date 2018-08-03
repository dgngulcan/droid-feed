package com.droidfeed.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread

abstract class LocalBoundResource<ResultType> {

    private val result = MediatorLiveData<Resource<ResultType?>>()

    init {
        result.value = Resource.loading()

        val dbSource = loadFromDb()

        result.addSource(dbSource) { newData -> result.setValue(Resource.success(newData)) }
    }

    @Suppress("UNCHECKED_CAST")
    fun asLiveData(): LiveData<Resource<ResultType>> = result as LiveData<Resource<ResultType>>

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>
}