package com.droidfeed.ui.module.newsletter

import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.api.mailchimp.Error
import com.droidfeed.data.api.mailchimp.ErrorAdapter
import com.droidfeed.data.api.mailchimp.ErrorType
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.SubscriptionStatus
import com.droidfeed.data.repo.NewsletterRepo
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.ui.common.DataState
import com.droidfeed.util.event.Event
import com.squareup.moshi.Moshi
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
        val subscriber = Subscriber(email, SubscriptionStatus.SUBSCRIBED)

        signUpEvent.postValue(Event(DataState.Loading()))

        val call = newsletterRepo.addSubscriberToNewsletter(subscriber)

        call.await().let { response ->
            if (response.isSuccessful) {
                signUpEvent.postValue(Event(DataState.Success<Any>()))
            } else {
                when (response.code()) {
                    400 -> {
                        val errorBody = response.errorBody()?.string()
                        val mcError = parseMailchimpError(errorBody)

                        when {
                            mcError != null -> handleMailChimpError(mcError)
                            else -> signUpEvent.postValue(Event(DataState.Error<Any>()))
                        }
                    }

                    else -> signUpEvent.postValue(Event(DataState.Error<Any>()))
                }
            }
        }
    }

    private fun parseMailchimpError(errorBody: String?): Error? {
        val moshi = Moshi.Builder()
            .add(ErrorAdapter())
            .build()

        val jsonAdapter = moshi.adapter<Error>(Error::class.java)
        return jsonAdapter.fromJson(errorBody)
    }

    private fun handleMailChimpError(error: Error) {
        when (error.type) {
            ErrorType.MEMBER_ALREADY_EXIST -> {
                signUpEvent.postValue(Event(DataState.Error(data = ErrorType.MEMBER_ALREADY_EXIST)))
            }
            ErrorType.INVALID_RESOURCE -> {
                signUpEvent.postValue(Event(DataState.Error(data = ErrorType.INVALID_RESOURCE)))
            }
        }
    }
}