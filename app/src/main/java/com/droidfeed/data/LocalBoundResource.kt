package com.droidfeed.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.support.annotation.MainThread

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
abstract class LocalBoundResource<ResultType> {

    private val result = MutableLiveData<Resource<ResultType?>>()

    init {
        result.value = Resource.loading(null)

        val dbSource = loadFromDb()

        Transformations.map(dbSource, { newData ->
            result.value = Resource.success(newData)
        })
    }

    @Suppress("UNCHECKED_CAST")
    fun asLiveData(): LiveData<Resource<ResultType>> = result as LiveData<Resource<ResultType>>

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

}


