package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.repo.LicenseRepository
import com.droidfeed.ui.module.about.license.LicensesViewModel
import com.droidfeed.util.event.Event
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class LicensesViewModelTest {

    @Rule @JvmField var instantTaskExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var licenseRepository: LicenseRepository

    lateinit var sut: LicensesViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun whenInstantiated_shouldRetrieveLicenses() {
        sut = LicensesViewModel(licenseRepository)

        verify(exactly = 1) { licenseRepository.getLicenses() }
    }

    @Test
    fun whenBackNavClicked_shouldFireBackNavEvent() {
        sut = LicensesViewModel(licenseRepository)
        val observer = mockk<Observer<Event<Unit>>>(relaxed = true)
        sut.onBackNavigation.observeForever(observer)

        sut.onBackNavigation()

        verify(exactly = 1) { observer.onChanged(any()) }
    }


}