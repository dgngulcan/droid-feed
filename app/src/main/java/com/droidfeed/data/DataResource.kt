package com.droidfeed.data

import org.jetbrains.annotations.Nullable

/**
 * A generic class that holds data with its state.
 */
@Suppress("DataClassPrivateConstructor")
data class DataResource<out T> private constructor(
    val dataState: DataStatus,
    @Nullable val data: T? = null
) {

    companion object {
        fun <T> success(data: T? = null): DataResource<T> {
            return DataResource(DataStatus.Success, data)
        }

        fun <T> loading(): DataResource<T> {
            return DataResource(DataStatus.Loading)
        }

        fun <T> error(@Nullable data: Any? = null): DataResource<T> {
            return DataResource(DataStatus.Error(data))
        }
    }
}