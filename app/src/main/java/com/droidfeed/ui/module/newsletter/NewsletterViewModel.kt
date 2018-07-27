package com.droidfeed.ui.module.newsletter

import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.api.mailchimp.MailchimpError
import com.droidfeed.data.api.mailchimp.MailchimpErrorType
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.SubscriptionStatus
import com.droidfeed.data.repo.NewsletterRepo
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.DataState
import com.droidfeed.util.event.Event
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class NewsletterViewModel @Inject constructor(
    private val newsletterRepo: NewsletterRepo
) : BaseViewModel() {

    val signUpEvent = MutableLiveData<Event<DataState>>()

    /**
     * Sign up given email to DroidFeed newsletter.
     *
     * @param email
     */
    fun signUp(email: String) = launch {
        signUpEvent.value = Event(DataState.Loading())

        val subscriber = Subscriber(email, SubscriptionStatus.SUBSCRIBED)
        val call = newsletterRepo.addSubscriber(subscriber)

        call.await().let { response ->
            if (response.isSuccessful) {
                signUpEvent.postValue(Event(DataState.Success<Any>()))
            } else {
                when (response.code()) {
                    400 -> {
                        if (response.body() is MailchimpError) {
                            signUpEvent.postValue(
                                Event(DataState.Error(data = MailchimpErrorType.MEMBER_ALREADY_EXIST))
                            )
                        }
                    }

                    else -> signUpEvent.value =
                        Event(DataState.Error<Any>())
                }
            }
        }
    }
}