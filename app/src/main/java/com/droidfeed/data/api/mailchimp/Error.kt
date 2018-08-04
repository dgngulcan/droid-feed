package com.droidfeed.data.api.mailchimp

data class Error(
    val type: ErrorType,
    val status: Int,
    val detail: String
)