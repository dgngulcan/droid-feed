package com.droidfeed.data.api.mailchimp

import com.squareup.moshi.Json

data class Subscriber(
    @field:Json(name = "email_address")
    var email: String,
    var status: SubscriptionStatus
)