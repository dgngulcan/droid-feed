package com.droidfeed.data.api.mailchimp

import com.squareup.moshi.Json

enum class SubscriptionStatus(private val status: String) {
    @Json(name = "subscribed")
    SUBSCRIBED("subscribed"),

    @Json(name = "unsubscribed")
    UNSUBSCRIBED("unsubscribed");

    override fun toString(): String = status
}