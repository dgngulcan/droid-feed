package com.droidfeed.data.api.mailchimp

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
