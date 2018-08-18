package com.droidfeed.data

/**
 * Sealed class for data call statuses.
 */
sealed class DataStatus {

    object Loading : DataStatus()

    object Success : DataStatus()

    data class Error<out T>(val data: T? = null) : DataStatus()
}