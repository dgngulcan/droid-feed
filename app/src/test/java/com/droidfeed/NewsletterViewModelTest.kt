package com.droidfeed

import android.util.Patterns
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.repo.NewsletterRepo
import com.droidfeed.ui.module.newsletter.NewsletterViewModel
import com.droidfeed.util.AnalyticsUtil
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class NewsletterViewModelTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val newsletterRepo = Mockito.mock(NewsletterRepo::class.java)
    private val analyticsUtil = Mockito.mock(AnalyticsUtil::class.java)
    private lateinit var viewModel: NewsletterViewModel

    @Before
    fun setup() {
        viewModel = NewsletterViewModel(newsletterRepo, analyticsUtil)
    }

    @Test
    fun match_default_view_state() {
        Assert.assertFalse(viewModel.isProgressVisible.value!!)
        Assert.assertTrue(viewModel.isSignButtonVisible.value!!)
    }

    @Test
    fun GIVEN_empty_email_input_WHEN_sign_up_clicked_THEN_show_empty_email_error() = runBlocking {
        val observer = mock<Observer<Int>>()
        viewModel.errorText.observeForever(observer)

        viewModel.signUp("")

        verify(observer).onChanged(R.string.error_empty_email)
    }

    @Test
    fun GIVEN_valid_email_input_WHEN_sign_up_clicked_THEN_show_progress_hide_button() =
        runBlocking {
            val progressObserver = mock<Observer<Boolean>>()
            val buttonObserver = mock<Observer<Boolean>>()
            viewModel.isProgressVisible.observeForever(progressObserver)
            viewModel.isSignButtonVisible.observeForever(buttonObserver)

            viewModel.signUp("valid@email.com")

            verify(progressObserver).onChanged(true)
            verify(buttonObserver).onChanged(false)
        }
}