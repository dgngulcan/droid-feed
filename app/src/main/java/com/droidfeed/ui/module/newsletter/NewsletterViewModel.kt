package com.droidfeed.ui.module.newsletter

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.droidfeed.data.api.mailchimp.MailchimpError
import com.droidfeed.data.api.mailchimp.MailchimpErrorType
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.SubscriptionStatus
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.DataState
import com.droidfeed.ui.common.Event
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 4/15/18.
 */
class NewsletterViewModel @Inject constructor(
    private val newsletterService: NewsletterService
) : BaseViewModel() {

    companion object {
        const val NEWSLETTER_LIST_ID = "b45d2a62b0"
    }

    val signUpEvent = MutableLiveData<Event<DataState>>()
    val userEmail = ObservableField<String>("")

    fun signUp(email: String) = launch {
        signUpEvent.value = Event(DataState.Loading())
        val subscriber =
            Subscriber(email, SubscriptionStatus.SUBSCRIBED)
        val call = newsletterService.addSubscriber(NEWSLETTER_LIST_ID, subscriber)

        call.await().let { response ->
            if (response.isSuccessful) {
                signUpEvent.postValue(Event(DataState.Success<Any>()))

            } else {
                when (response.code()) {
                    400 -> {
                        if (response.body() is MailchimpError) {
//                            response.body().
                            signUpEvent.postValue(
                                Event(DataState.Error(data = MailchimpErrorType.MEMBER_ALREADY_EXIST))
                            )
                        }
                    }

                    else -> signUpEvent.value = Event(DataState.Error<Any>())
                }
            }
        }
    }


}