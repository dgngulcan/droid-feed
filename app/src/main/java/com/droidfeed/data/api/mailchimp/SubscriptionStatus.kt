package com.droidfeed.data.api.mailchimp

enum class SubscriptionStatus(private val status: String) {
    SUBSCRIBED("subscribed"),
    UNSUBSCRIBED("unsubscribed");

    fun getValue() = status
}