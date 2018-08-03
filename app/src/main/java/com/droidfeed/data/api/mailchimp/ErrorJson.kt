package com.droidfeed.data.api.mailchimp

data class ErrorJson(
    val title: String,
    val status: Int,
    val detail: String
)