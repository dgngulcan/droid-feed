package com.droidfeed.data

@Suppress("unused")
sealed class DataStatus<out T> {

    data class Loading<out T>(val progress: Int? = null) : DataStatus<T>()

    data class Successful<T>(val data: T? = null) : DataStatus<T>()

    data class Failed<out T>(val throwable: Throwable? = null) : DataStatus<T>()

    data class HttpFailed<out T>(val code: Int) : DataStatus<T>()
}