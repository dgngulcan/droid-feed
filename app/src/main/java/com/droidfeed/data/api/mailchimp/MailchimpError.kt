package com.droidfeed.data.api.mailchimp

/**
 * Defines base error response for Mailchimp API.
 */
data class MailchimpError(
    val type: ErrorType,
    val code: Int,
    val detail: String
) : Throwable()