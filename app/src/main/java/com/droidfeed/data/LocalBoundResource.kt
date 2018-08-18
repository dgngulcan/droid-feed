package com.droidfeed.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread

abstract class LocalBoundResource<ResultType> {

    private val result = MediatorLiveData<DataResource<ResultType?>>()

    init {
        result.value = DataResource.loading()

        val dbSource = loadFromDb()

        result.addSource(dbSource) { newData -> result.setValue(DataResource.success(newData)) }
    }

    @Suppress("UNCHECKED_CAST")
    fun asLiveData(): LiveData<DataResource<ResultType>> = result as LiveData<DataResource<ResultType>>

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>
}