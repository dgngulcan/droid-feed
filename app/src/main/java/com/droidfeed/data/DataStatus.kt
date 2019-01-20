package com.droidfeed.data

@Suppress("unused")
sealed class DataStatus<out T> {

    /**
     * Represents loading state of the data. It should only be used when the loading starts. All
     * other states are stating end of loading.
     *
     * @param progress
     */
    data class Loading<out T>(val progress: Int? = null) : DataStatus<T>()

    /**
     * Represents success state for the data.
     *
     * @param data
     */
    data class Successful<T>(val data: T? = null) : DataStatus<T>()

    /**
     * Represents fail state for the data.
     *
     * @param throwable
     */
    data class Failed<out T>(val throwable: Throwable? = null) : DataStatus<T>()

    /**
     * Represents fail state for Http call i.e. non 200 status codes.
     *
     * @param code http status code
     */
    data class HttpFailed<out T>(val code: Int) : DataStatus<T>()
}