package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.module.onboard.OnBoardViewModel
import com.droidfeed.util.event.EventObserver
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Suppress("TestFunctionName")
@RunWith(JUnit4::class)
class OnBoardViewModelTest {

    @Rule @JvmField var instantTaskExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var sourceRepo: SourceRepo

    private lateinit var sut: OnBoardViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)
    }

    @After
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun match_default_view_state() {
        sut = OnBoardViewModel(sourceRepo)
        Assert.assertFalse(sut.isProgressVisible.value!!)
        Assert.assertFalse(sut.isContinueButtonEnabled.value!!)
        Assert.assertTrue(sut.isAgreementCBEnabled.value!!)
        Assert.assertEquals(R.drawable.onboard_bg, sut.backgroundImageId)
    }

    @Test
    fun WHEN_initiated_THEN_pull_sources() {
        sut = OnBoardViewModel(sourceRepo)

        verify(exactly = 1) { runBlocking { sourceRepo.pull() } }
    }

    @Test
    fun WHEN_agreed_terms_THEN_enable_continue_button() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        sut = OnBoardViewModel(sourceRepo)
        sut.isContinueButtonEnabled.observeForever(observer)

        sut.onAgreementChecked(true)

        verify(exactly = 1) { observer.onChanged(true) }
    }

    @Test
    fun WHEN_disagreed_terms_THEN_disable_continue_button() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        sut = OnBoardViewModel(sourceRepo)
        sut.onAgreementChecked(true)

        sut.isContinueButtonEnabled.observeForever(observer)

        sut.onAgreementChecked(false)
        verify(exactly = 1) { observer.onChanged(false) }
    }

    @Test
    fun WHEN_continue_button_is_clicked_THEN_disable_continue_button() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        sut = OnBoardViewModel(sourceRepo)
        sut.onAgreementChecked(true)
        sut.isContinueButtonEnabled.observeForever(observer)

        sut.onContinueClicked()

        verify(exactly = 1) { observer.onChanged(false) }
    }

    @Test
    fun WHEN_continue_button_is_clicked_THEN_open_main_activity() {
        every { runBlocking { sourceRepo.pull() } } returns DataStatus.Successful(emptyList())
        sut = OnBoardViewModel(sourceRepo)
        val observer = mockk<EventObserver<Unit>>(relaxed = true)
        sut.openMainActivity.observeForever(observer)

        sut.onAgreementChecked(true)
        sut.onContinueClicked()

        verify(exactly = 1) { observer.onChanged(any()) }
    }
}