package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.repo.LicenceRepository
import com.droidfeed.ui.module.about.licence.LicencesViewModel
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
class LicencesViewModelTest {

    @Rule @JvmField var instantTaskExecutorRule = InstantTaskExecutorRule()
    @MockK lateinit var licenceRepository: LicenceRepository

    lateinit var sut: LicencesViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Test
    fun whenInstantiated_shouldRetrieveLicences() {
        sut = LicencesViewModel(licenceRepository)

        verify(exactly = 1) { licenceRepository.getLicences() }
    }

    @Test
    fun whenBackNavClicked_shouldFireBackNavEvent() {
        sut = LicencesViewModel(licenceRepository)
        val observer = mockk<Observer<Event<Unit>>>(relaxed = true)
        sut.onBackNavigation.observeForever(observer)

        sut.onBackNavigation()

        verify(exactly = 1) { observer.onChanged(any()) }
    }


}