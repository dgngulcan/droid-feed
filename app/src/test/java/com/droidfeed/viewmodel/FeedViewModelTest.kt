package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.module.feed.FeedViewModel
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FeedViewModelTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK lateinit var sourceRepo: SourceRepo
    @MockK lateinit var postRepo: PostRepo
    @MockK lateinit var postRepo: PostRepo

    lateinit var sut: FeedViewModel

    @Before
    fun setup() {
        sut = FeedViewModel(
            sourceRepo,
            postRepo
        )
    }


}