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
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun defaultState_shouldMatch() {
        sut = OnBoardViewModel(sourceRepo)
        Assert.assertFalse(sut.isProgressVisible.value!!)
        Assert.assertFalse(sut.isContinueButtonEnabled.value!!)
        Assert.assertTrue(sut.isAgreementCBEnabled.value!!)
        Assert.assertEquals(R.drawable.onboard_bg, sut.backgroundImageId)
    }

    @Test
    fun whenInstantiated_shouldPullSources() {
        sut = OnBoardViewModel(sourceRepo)

        verify(exactly = 1) { runBlocking { sourceRepo.pull() } }
    }

    @Test
    fun whenAgreedTerms_shouldEnableContinueButton() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        sut = OnBoardViewModel(sourceRepo)
        sut.isContinueButtonEnabled.observeForever(observer)

        sut.onAgreementChecked(true)

        verify(exactly = 1) { observer.onChanged(true) }
    }

    @Test
    fun whenDisagreedTerms_shouldDisableContinueButton() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        sut = OnBoardViewModel(sourceRepo)
        sut.onAgreementChecked(true)

        sut.isContinueButtonEnabled.observeForever(observer)

        sut.onAgreementChecked(false)
        verify(exactly = 1) { observer.onChanged(false) }
    }

    @Test
    fun when_continueButtonClicked_shouldDisableContinueButton() {
        val observer = mockk<Observer<Boolean>>(relaxed = true)
        sut = OnBoardViewModel(sourceRepo)
        sut.onAgreementChecked(true)
        sut.isContinueButtonEnabled.observeForever(observer)

        sut.onContinueClicked()

        verify(exactly = 1) { observer.onChanged(false) }
    }

    @Test
    fun whenContinueButtonIsClicked_shouldFireOpenMainActivityEvent() {
        every { runBlocking { sourceRepo.pull() } } returns DataStatus.Successful(emptyList())
        sut = OnBoardViewModel(sourceRepo)
        val observer = mockk<EventObserver<Unit>>(relaxed = true)
        sut.openMainActivity.observeForever(observer)

        sut.onAgreementChecked(true)
        sut.onContinueClicked()

        verify(exactly = 1) { observer.onChanged(any()) }
    }
}