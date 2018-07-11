package com.droidfeed.data.api.mailchimp

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson

internal class ErrorAdapter {

    @ToJson
    fun toJson(error: MailchimpError): MailchimpErrorJson {
        val errorType = when (error.type) {
            MailchimpErrorType.MEMBER_ALREADY_EXIST -> "Member Exists"
        }

        return MailchimpErrorJson(errorType, error.status, error.detail)
    }

    @FromJson
    fun fromJson(errorJson: MailchimpErrorJson): MailchimpError {
        val errorType = when (errorJson.title) {
            "Member Exists" -> MailchimpErrorType.MEMBER_ALREADY_EXIST
            else -> throw JsonDataException("unknown suit: $errorJson.title")
        }

        return MailchimpError(errorType, errorJson.status, errorJson.detail)
    }
}