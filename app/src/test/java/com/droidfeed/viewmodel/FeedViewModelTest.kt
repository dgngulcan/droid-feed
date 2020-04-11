package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.model.Post
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SharedPrefsRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.module.feed.AppRateInteractor
import com.droidfeed.ui.module.feed.FeedType
import com.droidfeed.ui.module.feed.FeedViewModel
import com.droidfeed.ui.module.feed.analytics.FeedScreenLogger
import com.droidfeed.util.event.Event
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FeedViewModelTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK lateinit var sourceRepo: SourceRepo
    @MockK lateinit var postRepo: PostRepo
    @MockK lateinit var sharedPrefsRepo: SharedPrefsRepo
    @MockK lateinit var feedScreenLogger: FeedScreenLogger
    @MockK lateinit var appRateInteractor: AppRateInteractor

    lateinit var sut: FeedViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)

        sut = FeedViewModel(
            sourceRepo,
            postRepo,
            sharedPrefsRepo,
            feedScreenLogger,
            appRateInteractor
        )
    }

    @Test
    fun whenInstantiated_shouldRefreshWithActiveSources() {
        verify(exactly = 1) { sourceRepo.getActives() }
        verify(exactly = 1) { runBlocking { postRepo.refresh(any(), any()) } }
    }

    @Test
    fun whenBookmarked_shouldShowUndoSnack() {
        val post = mockk<Post>(relaxed = true) {
            every { isBookmarked() } returns true
        }

        sut.setFeedType(FeedType.BOOKMARKS)

        val observer = mockk<Observer<in Event<() -> Unit>>>(relaxed = true)
        sut.showUndoBookmarkSnack.observeForever(observer)

        sut.togglePostBookmark(post)
        verify(exactly = 1) { observer.onChanged(any()) }
    }


}