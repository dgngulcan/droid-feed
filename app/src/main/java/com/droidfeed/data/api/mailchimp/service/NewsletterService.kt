package com.droidfeed.data.api.mailchimp.service

import com.droidfeed.data.api.mailchimp.Subscriber
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path


/**
 * Service for Mailchimp newsletter APIs.
 *
 * Created by Dogan Gulcan on 4/15/18.
 */
interface NewsletterService {

    @POST("lists/{listId}/members/")
    fun addSubscriber(
        @Path("listId") listId: String,
        @Body subscriber: Subscriber
    ): Deferred<Response<Any>>

}