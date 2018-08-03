package com.droidfeed.ui.common

sealed class DataState {

    class Loading : DataState()

    data class Success<out T>(val data: T? = null) : DataState()

    data class Error<out T>(val message: String = "", val data: T? = null) : DataState()
}