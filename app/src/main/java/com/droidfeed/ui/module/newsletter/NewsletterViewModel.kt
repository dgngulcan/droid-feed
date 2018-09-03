package com.droidfeed.ui.module.newsletter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.droidfeed.data.DataResource
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.SubscriptionStatus
import com.droidfeed.data.repo.NewsletterRepo
import com.droidfeed.ui.common.BaseViewModel
import javax.inject.Inject

class NewsletterViewModel @Inject constructor(
    private val newsletterRepo: NewsletterRepo
) : BaseViewModel() {

    private val signUpEmail = MutableLiveData<String>()

    val signUpEvent: LiveData<DataResource<Any>> = Transformations.switchMap(
        signUpEmail
    ) { email ->
        val subscriber = Subscriber(email, SubscriptionStatus.SUBSCRIBED)
        newsletterRepo.addSubscriberToNewsletter(subscriber)
    }

    /**
     * Sign up given email to DroidFeed newsletter.
     *
     * @param email
     */
    fun signUp(email: String) {
        signUpEmail.value = email
    }
}