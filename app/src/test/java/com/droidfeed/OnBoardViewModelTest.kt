package com.droidfeed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.DataStatus
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.module.onboard.OnBoardViewModel
import com.droidfeed.util.event.EventObserver
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class OnBoardViewModelTest {

    @Rule @JvmField var instantTaskExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var sourceRepo: SourceRepo
    private lateinit var viewModel: OnBoardViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)
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
        coVerify(exactly = 1) { sourceRepo.pull() }

        viewModel = OnBoardViewModel(sourceRepo)
    }

    @Test
    fun WHEN_agreed_terms_THEN_enable_continue_button() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.isContinueButtonEnabled.observeForever(observer)

        viewModel.onAgreementChecked(true)

        verify(exactly = 1) { observer.onChanged(true) }
    }

    @Test
    fun WHEN_disagreed_terms_THEN_disable_continue_button() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.onAgreementChecked(true)

        viewModel.isContinueButtonEnabled.observeForever(observer)

        viewModel.onAgreementChecked(false)
        verify(exactly = 1) { observer.onChanged(false) }
    }

    @Test
    fun WHEN_continue_button_is_clicked_THEN_disable_continue_button() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        viewModel.onAgreementChecked(true)
        viewModel.isContinueButtonEnabled.observeForever(observer)

        viewModel.onContinueClicked()

        verify(exactly = 1) { observer.onChanged(false) }
    }

    @Test
    fun WHEN_continue_button_is_clicked_THEN_open_main_activity() {
        coEvery { runBlocking { sourceRepo.pull() } } returns DataStatus.Successful(emptyList())
        viewModel = OnBoardViewModel(sourceRepo)
        val observer = mockk<EventObserver<Unit>>(relaxed = true)
        viewModel.openMainActivity.observeForever(observer)

        viewModel.onAgreementChecked(true)
        viewModel.onContinueClicked()

        verify(exactly = 1) { observer.onChanged(any()) }
    }
}