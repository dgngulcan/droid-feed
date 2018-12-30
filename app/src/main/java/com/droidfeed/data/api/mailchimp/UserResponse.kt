package com.droidfeed.data.api.mailchimp

import com.squareup.moshi.Json

data class UserResponse(
    val id: String,

    @field:Json(name = "email_address")
    val email: String,

    @field:Json(name = "unique_email_id")
    val emailId: String,

    val status: String
)