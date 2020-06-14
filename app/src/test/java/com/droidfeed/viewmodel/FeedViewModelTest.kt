package com.droidfeed.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.droidfeed.data.model.Post
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SharedPrefsRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.module.feed.AppRateInteractor
import com.droidfeed.ui.module.feed.FeedViewModel
import com.droidfeed.util.event.Event
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
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
    @MockK lateinit var appRateInteractor: AppRateInteractor

    lateinit var sut: FeedViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true, relaxUnitFun = true)

        sut = FeedViewModel(
            sourceRepo = sourceRepo,
            postRepo = postRepo,
            sharedPrefs = sharedPrefsRepo,
            appRateInteractor = appRateInteractor
        )
    }

    @After
    fun cleanUp() {
        unmockkAll()
    }

    @Test
    fun whenInstantiated_shouldRefreshWithActiveSources() {
        sut.isDisplayingBookmarkedItems(false)

        verify(exactly = 1) { sourceRepo.getActives() }
        verify(exactly = 1) { runBlocking { postRepo.refresh(any(), any()) } }
    }

    @Test
    fun givenBookmarksFeed_whenBookmarked_shouldShowUndoSnack() {
        val post = mockk<Post>(relaxed = true) {
            every { isBookmarked() } returns true
        }
        sut.isDisplayingBookmarkedItems(true)
        sut.feedType.observeForever { }
        val observer = mockk<Observer<in Event<() -> Unit>>>(relaxed = true)

        sut.showUndoBookmarkSnack.observeForever(observer)

        sut.togglePostBookmark(post)
        verify(exactly = 1) { observer.onChanged(any()) }
    }

}