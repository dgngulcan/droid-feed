package com.droidfeed.data.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor for adding authentication headers for retrofit requests.
 */
class AuthenticationInterceptor(private val authToken: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder().header("Authorization", authToken)

        val request = builder.build()
        return chain.proceed(request)
    }
}