package com.droidfeed.data.repo

import com.droidfeed.data.DataStatus
import com.droidfeed.data.api.mailchimp.ErrorAdapter
import com.droidfeed.data.api.mailchimp.MailchimpError
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.service.NewsletterService
import com.droidfeed.util.extention.suspendingEnqueue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.squareup.moshi.Moshi
import retrofit2.HttpException
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
    suspend fun addSubscriberToNewsletter(subscriber: Subscriber): DataStatus<MailchimpError> =
        try {
            val listId = remoteConfig.getString("mc_newsletter_list_id")
            newsletterService.addSubscriber(listId, subscriber).suspendingEnqueue()
            DataStatus.Successful()
        } catch (e: HttpException) {
            when (e.code()) {
                HTTP_BAD_REQUEST -> {
                    val errorBody = e.response().errorBody()?.string()
                    val mcError = errorBody?.let { parseError(it) }
                    when {
                        mcError != null -> DataStatus.HttpFailed(errorBody = mcError)
                        else -> DataStatus.HttpFailed(e.code())
                    }
                }
                else -> DataStatus.HttpFailed(e.code())
            }
        } catch (t: Throwable) {
            DataStatus.Failed(t)
        }

    private fun parseError(errorBody: String): MailchimpError? {
        val moshi = Moshi.Builder()
            .add(ErrorAdapter())
            .build()

        val jsonAdapter = moshi.adapter<MailchimpError>(MailchimpError::class.java)
        return jsonAdapter.fromJson(errorBody)
    }
}
