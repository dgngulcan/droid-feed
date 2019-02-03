package com.droidfeed.data.api.mailchimp

import androidx.annotation.Keep
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException

@Suppress("unused")
internal class ErrorAdapter {

    @FromJson
    @Keep
    fun fromJson(errorJson: ErrorJson): MailchimpError {
        val errorType = when (errorJson.title) {
            "Member Exists" -> ErrorType.MEMBER_ALREADY_EXIST
            "Invalid DataResource" -> ErrorType.INVALID_DATA_RESOURCE
            "Invalid Resource" -> ErrorType.INVALID_RESOURCE
            else -> throw JsonDataException("unknown suit: $errorJson.title")
        }

        return MailchimpError(errorType, errorJson.status, errorJson.detail)
    }
}