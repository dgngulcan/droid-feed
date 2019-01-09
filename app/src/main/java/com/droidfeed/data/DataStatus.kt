package com.droidfeed.data

@Suppress("unused")
sealed class DataStatus<T> {

    data class Loading<T>(val progress: Int? = null) : DataStatus<T>()

    data class Successful<T>(val data: T? = null) : DataStatus<T>()

    data class Failed<T>(val throwable: Throwable? = null) : DataStatus<T>()

    data class HttpFailed<T>(val code: Int) : DataStatus<T>()
}