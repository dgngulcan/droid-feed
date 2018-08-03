package com.droidfeed.data.api.mailchimp.service

import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.UserResponse
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
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
    ): Deferred<Response<UserResponse>>
}