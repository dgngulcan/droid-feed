package com.droidfeed.data.api.mailchimp.model

import androidx.annotation.Keep

@Keep
data class ErrorJson(
    val title: String,
    val status: Int,
    val detail: String
)