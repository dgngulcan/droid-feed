package com.droidnews.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.droidnews.data.api.ApiResponse
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.annotations.Nullable

/**
 * Adapted from <a href="https://developer.android.com/topic/libraries/architecture/guide.html">
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
abstract class NetworkBoundResource<ResultType, RequestType>(val context: Context) {

    private val result = MediatorLiveData<Resource<ResultType?>>()

    init {
        result.value = Resource.loading(null)

        val dbSource = loadFromDb()

        result.addSource(dbSource, { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource, { newData -> result.setValue(Resource.success(newData)) })
            }
        })
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse: LiveData<ApiResponse<RequestType>> = createCall()

        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource,
                { newData ->
                    result.setValue(Resource.loading(newData))
                })

        result.addSource(apiResponse, { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)

            if (response?.isSuccessful()!!) {

                async(UI) {
                    bg {
                        processResponse(response)?.let { saveCallResult(it) }
                    }

                    result.addSource(loadFromDb()
                    ) { newData -> result.setValue(Resource.success(newData)) }
                }

            } else {
                onFetchFailed()
                result.addSource(dbSource,
                        { newData -> result.setValue(Resource.error(response.errorMessage, newData)) })
            }
        })
    }

    protected open fun onFetchFailed() {}

    @Suppress("UNCHECKED_CAST")
    fun asLiveData(): LiveData<Resource<RequestType>> {
        return result as LiveData<Resource<RequestType>>
    }

    @WorkerThread
    protected open fun processResponse(response: ApiResponse<RequestType>): RequestType? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(@Nullable data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}


