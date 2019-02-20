package com.droidfeed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.module.onboard.OnBoardViewModel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class OnBoardViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val sourceRepo = mock(SourceRepo::class.java)
    private val viewModel = OnBoardViewModel(sourceRepo)

    @Test
    fun match_default_view_state() {
        Assert.assertFalse(viewModel.isProgressVisible.value!!)
        Assert.assertFalse(viewModel.isContinueButtonEnabled.value!!)
        Assert.assertTrue(viewModel.isAgreementCBEnabled.value!!)
        Assert.assertEquals(R.drawable.onboard_bg, viewModel.backgroundImageId)
    }

    @Test
    fun WHEN_initiated_THEN_pull_sources() {
    }

    @Test
    fun WHEN_agreed_terms_THEN_enable_continue_button() {
    }

    @Test
    fun WHEN_continue_button_is_clicked_THEN_disable_continue_button() {
    }

    @Test
    fun GIVEN_sources_are_pulled_WHEN_continue_button_is_clicked_THEN_open_main_activity() {
    }

    @Test
    fun GIVEN_sources_are_not_pulled_WHEN_continue_button_is_clicked_THEN_show_progress_and_open_main_activity() {
    }
}