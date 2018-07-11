package com.droidfeed.data.repo

import com.droidfeed.App
import com.droidfeed.R
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.droidfeed.data.api.mailchimp.Subscriber
import kotlinx.coroutines.experimental.Deferred
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 4/15/18.
 */
@Singleton
class NewsletterRepo @Inject constructor(
    private val appContext: App,
    private val newsletterService: NewsletterService
) {

    /**
     * Call to add new subscriber to the newsletter list.
     */
    fun addSubscriber(subscriber: Subscriber): Deferred<Any> {
        val listId = appContext.getString(R.string.newsletter_list_id)
        return newsletterService.addSubscriber(listId, subscriber)
    }
}
