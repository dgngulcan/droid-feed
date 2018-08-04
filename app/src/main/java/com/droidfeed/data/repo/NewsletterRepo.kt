package com.droidfeed.data.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.api.mailchimp.Error
import com.droidfeed.data.api.mailchimp.ErrorAdapter
import com.droidfeed.data.api.mailchimp.ErrorType
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.droidfeed.ui.common.DataState
import com.droidfeed.util.event.Event
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.launch
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
    fun addSubscriberToNewsletter(subscriber: Subscriber): LiveData<Event<DataState>> {
        val callLiveData = MutableLiveData<Event<DataState>>()

        callLiveData.value = Event(DataState.Loading())

        launch {
            val listId = remoteConfig.getString("mc_newsletter_list_id")

            val call = newsletterService.addSubscriber(listId, subscriber)
            call.await().let { response ->
                if (response.isSuccessful) {
                    callLiveData.postValue(Event(DataState.Success<Any>()))
                } else {
                    when (response.code()) {
                        400 -> {
                            val errorBody = response.errorBody()?.string()
                            val mcError = errorBody?.let { parseError(it) }

                            when {
                                mcError != null -> postError(mcError, callLiveData)
                                else -> callLiveData.postValue(Event(DataState.Error<Any>()))
                            }
                        }

                        else -> callLiveData.postValue(Event(DataState.Error<Any>()))
                    }
                }
            }
        }

        return callLiveData
    }

    private fun parseError(errorBody: String): Error? {
        val moshi = Moshi.Builder()
            .add(ErrorAdapter())
            .build()

        val jsonAdapter = moshi.adapter<Error>(Error::class.java)
        return jsonAdapter.fromJson(errorBody)
    }

    private fun postError(
        error: Error,
        callLiveData: MutableLiveData<Event<DataState>>
    ) {
        when (error.type) {
            ErrorType.MEMBER_ALREADY_EXIST -> {
                callLiveData.postValue(Event(DataState.Error(data = ErrorType.MEMBER_ALREADY_EXIST)))
            }
            ErrorType.INVALID_RESOURCE -> {
                callLiveData.postValue(Event(DataState.Error(data = ErrorType.INVALID_RESOURCE)))
            }
        }
    }
}
