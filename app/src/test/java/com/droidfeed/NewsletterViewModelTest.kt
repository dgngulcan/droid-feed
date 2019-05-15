package com.droidfeed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.repo.NewsletterRepo
import com.droidfeed.ui.module.newsletter.NewsletterViewModel
import com.droidfeed.util.AnalyticsUtil
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class NewsletterViewModelTest {

    @Rule @JvmField var instantTaskExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var newsletterRepo: NewsletterRepo
    @MockK lateinit var analyticsUtil: AnalyticsUtil

    private lateinit var viewModel: NewsletterViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = NewsletterViewModel(newsletterRepo, analyticsUtil)
    }

    @Test
    fun match_default_view_state() {
        Assert.assertFalse(viewModel.isProgressVisible.value!!)
        Assert.assertTrue(viewModel.isSignButtonVisible.value!!)
    }

    @Test
    fun GIVEN_empty_email_input_WHEN_sign_up_clicked_THEN_show_empty_email_error() = runBlocking {
        val observer = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorText.observeForever(observer)

        viewModel.signUp("")

        verify(exactly = 1) { observer.onChanged(R.string.error_empty_email) }
    }

    @Test
    fun GIVEN_valid_email_input_WHEN_sign_up_clicked_THEN_show_progress_hide_button() =
        runBlocking {
            val progressObserver = mockk<Observer<Boolean>>(relaxed = true)
            val buttonObserver = mockk<Observer<Boolean>>(relaxed = true)
            viewModel.isProgressVisible.observeForever(progressObserver)
            viewModel.isSignButtonVisible.observeForever(buttonObserver)

            viewModel.signUp("valid@email.com")

            verify(exactly = 1) { progressObserver.onChanged(true) }
            verify(exactly = 1) { buttonObserver.onChanged(false) }
        }
}