package com.droidfeed.data.api.mailchimp

/**
 * Created by Dogan Gulcan on 5/6/18.
 */
data class MailchimpUserResponse(
    val id: String,
    val email_address: String,
    val unique_email_id: String,
    val status: String
)