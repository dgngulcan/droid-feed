package com.droidfeed.data.api.mailchimp

/**
 * Created by Dogan Gulcan on 5/6/18.
 */
data class MailchimpError(
    val type: MailchimpErrorType,
    val status: Int,
    val detail: String
)

data class MailchimpErrorJson(
    val title: String,
    val status: Int,
    val detail: String
)
