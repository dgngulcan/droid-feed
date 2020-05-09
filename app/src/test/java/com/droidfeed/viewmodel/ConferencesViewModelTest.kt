package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droidfeed.data.repo.ConferenceRepo
import com.droidfeed.ui.module.conferences.ConferencesViewModel
import com.droidfeed.ui.module.conferences.analytics.ConferencesScreenLogger
import com.droidfeed.util.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConferencesViewModelTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK lateinit var logger: ConferencesScreenLogger
    @MockK lateinit var repo: ConferenceRepo

    lateinit var sut: ConferencesViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun cleanUp() {
        unmockkAll()
    }

    @Test
    fun whenInstantiated_shouldFetchConferences() {
        sut = ConferencesViewModel(repo, logger)

        verify(exactly = 1) { runBlocking { repo.getUpcoming() } }
        assert(sut.isProgressVisible.getOrAwaitValue())

    }

}