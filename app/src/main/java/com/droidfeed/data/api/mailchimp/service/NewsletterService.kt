package com.droidfeed.data.api.mailchimp.service

import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Service for Mailchimp newsletter APIs.
 */
interface NewsletterService {

    @POST("lists/{listId}/members")
    fun addSubscriber(
        @Path("listId") listId: String,
        @Body subscriber: Subscriber
    ): Call<UserResponse>
}