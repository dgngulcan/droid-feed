package com.droidfeed.ui.module.newsletter

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import com.droidfeed.BuildConfig
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.data.api.mailchimp.ErrorType
import com.droidfeed.data.api.mailchimp.MailchimpError
import com.droidfeed.data.api.mailchimp.Subscriber
import com.droidfeed.data.api.mailchimp.SubscriptionStatus
import com.droidfeed.data.repo.NewsletterRepo
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.AnalyticsUtil
import com.droidfeed.util.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

class NewsletterViewModel @Inject constructor(
    private val newsletterRepo: NewsletterRepo,
    private val analyticsUtil: AnalyticsUtil
) : BaseViewModel() {

    val isSignButtonVisible = MutableLiveData<Boolean>().apply { value = true }
    val isEmailInputVisible = MutableLiveData<Boolean>().apply { value = true }
    val isProgressVisible = MutableLiveData<Boolean>().apply { value = false }
    val isSubsConfirmationVisible = MutableLiveData<Boolean>().apply { value = false }
    val errorText = MutableLiveData<@StringRes Int>().apply { value = R.string.empty_string }
    val showErrorSnack = MutableLiveData<Event<@StringRes Int>>()
    val openUrl = MutableLiveData<Event<String>>()

    fun onPreviousIssues() {
        openUrl.postValue(Event(BuildConfig.DROIDFEED_PREVIOUS_ISSUES))
    }

    /**
     * Sign up given email to DroidFeed newsletter.
     *
     * @param email
     */
    fun signUp(email: String) = launch(Dispatchers.IO) {
        isSignButtonVisible.postValue(false)
        isProgressVisible.postValue(true)
        errorText.postValue(R.string.empty_string)

        val isValid = when {
            email.isBlank() -> {
                errorText.postValue(R.string.error_empty_email)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                errorText.postValue(R.string.error_email_format)
                false
            }
            else -> true
        }

        if (isValid) {
            val subscriber = Subscriber(
                email,
                SubscriptionStatus.SUBSCRIBED
            )

            val dataStatus = newsletterRepo.addSubscriberToNewsletter(subscriber)
            handleSignUpResponse(dataStatus)
        } else {
            isSignButtonVisible.postValue(true)
        }

        isProgressVisible.postValue(false)
    }

    private fun handleSignUpResponse(dataStatus: DataStatus<MailchimpError>) {
        when (dataStatus) {
            is DataStatus.Successful -> {
                isEmailInputVisible.postValue(false)
                isSubsConfirmationVisible.postValue(true)
                analyticsUtil.logNewsletterSignUp()
            }
            is DataStatus.Failed -> {
                isSignButtonVisible.postValue(true)
                when (dataStatus.throwable) {
                    is UnknownHostException -> {
                        showErrorSnack.postValue(Event(R.string.info_no_internet))
                    }
                }
            }
            is DataStatus.HttpFailed -> {
                isSignButtonVisible.postValue(true)

                val errorMessage = when (dataStatus.errorBody?.type ?: Unit) {
                    ErrorType.MEMBER_ALREADY_EXIST -> R.string.newsletter_email_exist
                    ErrorType.INVALID_DATA_RESOURCE -> R.string.error_api_generic
                    ErrorType.INVALID_RESOURCE -> R.string.error_email_format
                    else -> R.string.error_api_generic
                }

                errorText.postValue(errorMessage)
            }
        }
    }
}