package com.droidfeed.data.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.droidfeed.data.DataResource
import com.droidfeed.data.api.mailchimp.Error
import com.droidfeed.data.api.mailchimp.ErrorAdapter
import com.droidfeed.data.api.mailchimp.ErrorType
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.service.NewsletterService
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
    fun addSubscriberToNewsletter(subscriber: Subscriber): LiveData<DataResource<Any>> {
        val callLiveData = MutableLiveData<DataResource<Any>>()

        callLiveData.value = DataResource.loading()

        launch {
            val listId = remoteConfig.getString("mc_newsletter_list_id")

            val call = newsletterService.addSubscriber(listId, subscriber)
            call.await().let { response ->
                if (response.isSuccessful) {
                    callLiveData.postValue(DataResource.success(Any()))
                } else {
                    when (response.code()) {
                        400 -> {
                            val errorBody = response.errorBody()?.string()
                            val mcError = errorBody?.let { parseError(it) }

                            when {
                                mcError != null -> postError(mcError, callLiveData)
                                else -> callLiveData.postValue(DataResource.error(mcError))
                            }
                        }

                        else -> callLiveData.postValue(DataResource.error())
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
        callLiveData: MutableLiveData<DataResource<Any>>
    ) {
        when (error.type) {
            ErrorType.MEMBER_ALREADY_EXIST -> {
                callLiveData.postValue(DataResource.error(data = ErrorType.MEMBER_ALREADY_EXIST))
            }
            ErrorType.INVALID_RESOURCE -> {
                callLiveData.postValue(DataResource.error(data = ErrorType.INVALID_RESOURCE))
            }
        }
    }
}
