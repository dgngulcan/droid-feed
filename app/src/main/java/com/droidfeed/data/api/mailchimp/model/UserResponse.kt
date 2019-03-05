package com.droidfeed.data.api.mailchimp.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class UserResponse(
    val id: String,

    @field:Json(name = "email_address")
    val email: String,

    @field:Json(name = "unique_email_id")
    val emailId: String,

    val status: String
)