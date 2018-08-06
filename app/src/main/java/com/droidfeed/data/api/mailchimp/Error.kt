package com.droidfeed.data.api.mailchimp

/**
 * Defines base error response for Mailchimp API.
 */
data class Error(
    val type: ErrorType,
    val status: Int,
    val detail: String
)