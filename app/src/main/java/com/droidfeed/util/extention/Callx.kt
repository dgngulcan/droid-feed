@file:Suppress("UNCHECKED_CAST")

package com.droidfeed.util.extention

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droidfeed.data.DataStatus
import com.droidfeed.util.logThrowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Executes the call and suspends until the call is resulted by either success of failure.
 */
suspend fun <T> Call<T>.suspendingEnqueue() = suspendCoroutine<T> { continuation ->
    enqueue(object : Callback<T?> {
        override fun onFailure(call: Call<T?>, throwable: Throwable) {
            throwable.run {
                continuation.resumeWithException(throwable)
                logThrowable(this)
            }
        }

        override fun onResponse(call: Call<T?>, response: Response<T?>) {
            when {
                response.isSuccessful -> continuation.resume(response.body() as T)
                else -> {
                    HttpException(response).run {
                        continuation.resumeWithException(this)
                        logThrowable(this)
                    }
                }
            }
        }
    })
}


/**
 * accepts a transform
 *
 */
@Suppress("UNCHECKED_CAST")
fun <T, L : Collection<T>, R> Call<L>.transformResult(
    transform: (T) -> R
): LiveData<DataStatus<L>> {
    val liveData = MutableLiveData<DataStatus<L>>()
        .also { it.postValue(DataStatus.Loading()) }

    GlobalScope.launch(Dispatchers.IO) {
        val dataStatus = try {
            val results = suspendingEnqueue()
                .map(transform)

            DataStatus.Successful(results)
        } catch (t: HttpException) {
            DataStatus.HttpFailed<L>(t.code())
        } catch (t: Throwable) {
            DataStatus.Failed<L>(t)
        } as DataStatus<L>

        liveData.postValue(dataStatus)
    }
    return liveData
}
