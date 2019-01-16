package com.droidfeed.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droidfeed.data.DataStatus
import com.droidfeed.data.api.mailchimp.Error
import com.droidfeed.data.api.mailchimp.ErrorAdapter
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
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
    fun addSubscriberToNewsletter(subscriber: Subscriber): LiveData<DataStatus<Nothing>> {
        val dataStatus = MutableLiveData<DataStatus<Nothing>>()
            .also { it.postValue(DataStatus.Loading()) }

        GlobalScope.launch(Dispatchers.IO) {
            val listId = remoteConfig.getString("mc_newsletter_list_id")

            val call = newsletterService.addSubscriber(listId, subscriber)

            call.execute().let { response ->
                if (response.isSuccessful) {
                    dataStatus.postValue(DataStatus.Successful())
                } else {
                    when (response.code()) {
                        HTTP_BAD_REQUEST -> {
                            val errorBody = response.errorBody()?.string()
                            val mcError = errorBody?.let { parseError(it) }

                            when {
                                mcError != null -> dataStatus.postValue(DataStatus.Failed(mcError))
                                else -> dataStatus.postValue(DataStatus.HttpFailed(response.code()))
                            }
                        }

                        else -> dataStatus.postValue(DataStatus.HttpFailed(response.code()))
                    }
                }
            }
        }

        return dataStatus
    }

    private fun parseError(errorBody: String): Error? {
        val moshi = Moshi.Builder()
            .add(ErrorAdapter())
            .build()

        val jsonAdapter = moshi.adapter<Error>(Error::class.java)
        return jsonAdapter.fromJson(errorBody)
    }

}
