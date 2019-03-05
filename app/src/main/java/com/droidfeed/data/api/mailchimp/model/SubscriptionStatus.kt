package com.droidfeed.data.api.mailchimp.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
enum class SubscriptionStatus(private val status: String) {
    @Json(name = "subscribed")
    SUBSCRIBED("subscribed"),

    @Json(name = "unsubscribed")
    UNSUBSCRIBED("unsubscribed");

    override fun toString(): String = status
}