package com.droidfeed.data.repo

import com.droidfeed.App
import com.droidfeed.R
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsletterRepo @Inject constructor(
    private val appContext: App,
    private val newsletterService: NewsletterService
) {

    /**
     * Call to add new subscriber to the newsletter list.

     * @param subscriber
     */
    fun addSubscriber(subscriber: Subscriber): Deferred<Response<Any>> {
        val listId = appContext.getString(R.string.newsletter_list_id)
        return newsletterService.addSubscriber(listId, subscriber)
    }
}
