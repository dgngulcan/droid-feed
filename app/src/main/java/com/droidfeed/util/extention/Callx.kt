@file:Suppress("UNCHECKED_CAST")

package com.droidfeed.util.extention

import com.droidfeed.util.logThrowable
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