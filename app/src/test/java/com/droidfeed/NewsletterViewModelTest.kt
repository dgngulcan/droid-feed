package com.droidfeed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droidfeed.data.repo.NewsletterRepo
import com.droidfeed.ui.module.newsletter.NewsletterViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class NewsletterViewModelTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val newsletterRepo = Mockito.mock(NewsletterRepo::class.java)
    private val viewModel = NewsletterViewModel(newsletterRepo)


    @Test
    fun match_default_view_state() {

    }

    @Test
    fun GIVEN_invalid_email_input_WHEN_sign_up_clicked_THEN_show_invalid_email_error() {

    }

    @Test
    fun GIVEN_valid_email_input_WHEN_sign_up_clicked_THEN_show_progress_hide_button() {

    }


}