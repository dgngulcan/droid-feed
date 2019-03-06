package com.droidfeed.data.api.mailchimp.model

import androidx.annotation.Keep

/**
 * Defines base error response for Mailchimp API.
 */
@Keep
data class MailchimpError(
    val type: ErrorType,
    val code: Int,
    val detail: String
) : Throwable()