package com.droidfeed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.DataStatus
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.module.onboard.OnBoardViewModel
import com.droidfeed.util.event.EventObserver
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*


@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class OnBoardViewModelTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val sourceRepo = mock(SourceRepo::class.java)
    private lateinit var viewModel: OnBoardViewModel

    @Before
    fun before() {
        runBlocking {
            `when`(sourceRepo.pull()).thenReturn(DataStatus.Successful())
        }

        viewModel = OnBoardViewModel(sourceRepo)
    }

    @Test
    fun match_default_view_state() {
        Assert.assertFalse(viewModel.isProgressVisible.value!!)
        Assert.assertFalse(viewModel.isContinueButtonEnabled.value!!)
        Assert.assertTrue(viewModel.isAgreementCBEnabled.value!!)
        Assert.assertEquals(R.drawable.onboard_bg, viewModel.backgroundImageId)
    }

    @Test
    fun WHEN_initiated_THEN_pull_sources() {
        runBlocking {
            verify(sourceRepo, only()).pull()
            viewModel = OnBoardViewModel(sourceRepo)
        }
    }

    @Test
    fun WHEN_agreed_terms_THEN_enable_continue_button() {
        val observer = mock<Observer<Boolean>>()
        viewModel.isContinueButtonEnabled.observeForever(observer)

        viewModel.onAgreementChecked(true)
        verify(observer).onChanged(true)
    }

    @Test
    fun WHEN_disagreed_terms_THEN_disable_continue_button() {
        val observer = mock<Observer<Boolean>>()
        viewModel.onAgreementChecked(true)

        viewModel.isContinueButtonEnabled.observeForever(observer)

        viewModel.onAgreementChecked(false)
        verify(observer).onChanged(false)
    }

    @Test
    fun WHEN_continue_button_is_clicked_THEN_disable_continue_button() {
        val observer = mock<Observer<Boolean>>()
        viewModel.onAgreementChecked(true)
        viewModel.isContinueButtonEnabled.observeForever(observer)

        viewModel.onContinueClicked()

        verify(observer).onChanged(false)
    }


    @Test
    fun WHEN_continue_button_is_clicked_THEN_open_main_activity() {
        val observer = mock<EventObserver<Any>>()
        viewModel.openMainActivity.observeForever(observer)
        viewModel.onAgreementChecked(true)

        viewModel.onContinueClicked()

        verify(observer, only()).onChanged(ArgumentMatchers.any())
    }

}