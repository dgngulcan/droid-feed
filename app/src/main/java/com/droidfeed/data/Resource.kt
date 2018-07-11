package com.droidfeed.data

import org.jetbrains.annotations.Nullable

/**
 * A generic class that describes a data with a status.
 *
 * Created by Dogan Gulcan on 9/22/17.
 */
class Resource<out T>(val status: Status, @Nullable val data: T? = null, @Nullable val message: String = "") {

    companion object {

        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> loading(@Nullable data: T? = null): Resource<T?> {
            return Resource(Status.LOADING, data)
        }

        fun <T> error(msg: String, @Nullable data: T): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

    }
}

/**
 * Status for data.
 */
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}