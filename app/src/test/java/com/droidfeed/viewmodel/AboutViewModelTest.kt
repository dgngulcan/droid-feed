package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droidfeed.ui.module.about.AboutViewModel
import com.droidfeed.ui.module.about.analytics.AboutScreenLogger
import com.droidfeed.util.IntentProvider
import com.droidfeed.util.event.EventObserver
import com.droidfeed.util.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AboutViewModelTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK lateinit var logger: AboutScreenLogger

    lateinit var sut: AboutViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        sut = AboutViewModel(logger)
    }

    @After
    fun cleanUp() {
        unmockkAll()
    }

    @Test
    fun whenAppRateClicked_thenStartRateIntent() {
        sut.rateApp()

        assert(sut.startIntent.getOrAwaitValue().peekContent() == IntentProvider.TYPE.RATE_APP)
    }

    @Test
    fun whenShareAppClicked_thenStartShareAppIntent() {
        sut.shareApp()

        assert(sut.startIntent.getOrAwaitValue().peekContent() == IntentProvider.TYPE.SHARE_APP)
    }

    @Test
    fun whenContactClicked_thenStartContactIntent() {
        sut.contactEmail()

        assert(sut.startIntent.getOrAwaitValue().peekContent() == IntentProvider.TYPE.CONTACT_EMAIL)
    }

    @Test
    fun whenOpenLicensesClicked_thenFireOpenLicensesEvent() {
        val observer = mockk<EventObserver<Unit>>(relaxed = true)
        sut.openLicenses.observeForever(observer)

        sut.openLicenses()

        verify(exactly = 1) { observer.onChanged(any()) }
    }

}