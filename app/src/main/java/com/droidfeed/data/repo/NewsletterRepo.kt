package com.droidfeed.data.repo

import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.UserResponse
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsletterRepo @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val newsletterService: NewsletterService
) {

    /**
     * Call to add new subscriber to the newsletter list.
     *
     * @param subscriber
     */
    fun addSubscriberToNewsletter(subscriber: Subscriber): Deferred<Response<UserResponse>> {
        val listId = remoteConfig.getString("mc_newsletter_list_id")
        return newsletterService.addSubscriber(listId, subscriber)
    }
}
