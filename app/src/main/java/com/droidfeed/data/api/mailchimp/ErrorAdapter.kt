package com.droidfeed.data.api.mailchimp

import androidx.annotation.Keep
import com.droidfeed.data.api.mailchimp.model.ErrorJson
import com.droidfeed.data.api.mailchimp.model.ErrorType
import com.droidfeed.data.api.mailchimp.model.MailchimpError
import com.droidfeed.util.logThrowable
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
            else -> {
                logThrowable(JsonDataException("unknown suit: $errorJson.title"))
                ErrorType.UNKNOWN_SUIT
            }
        }

        return MailchimpError(
            errorType,
            errorJson.status,
            errorJson.detail
        )
    }
}