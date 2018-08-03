package com.droidfeed.data.api.mailchimp

data class UserResponse(
    val id: String,
    val email_address: String,
    val unique_email_id: String,
    val status: String
)