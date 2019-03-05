package com.droidfeed.data.api.mailchimp.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Subscriber(
    @field:Json(name = "email_address")
    var email: String,
    var status: SubscriptionStatus
)