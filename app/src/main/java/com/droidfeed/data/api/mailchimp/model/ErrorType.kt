package com.droidfeed.data.api.mailchimp.model

import androidx.annotation.Keep

@Keep
enum class ErrorType {
    MEMBER_ALREADY_EXIST,
    INVALID_RESOURCE,
    INVALID_DATA_RESOURCE,
    UNKNOWN_SUIT
}