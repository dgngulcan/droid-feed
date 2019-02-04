package com.droidfeed.ui.module.main

import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import com.droidfeed.R
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.model.SourceUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import com.droidfeed.util.hasAcceptedTerms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    sourceRepo: SourceRepo,
    postRepo: PostRepo,
    sharedPrefs: SharedPreferences
) : BaseViewModel() {

    val toolbarTitle = MutableLiveData<@StringRes Int>().apply { value = R.string.app_name }
    val onNavigation = MutableLiveData<Destination>().apply { value = Destination.FEED }
    val scrollTop = MutableLiveData<Event<Unit>>()

    val isUserTermsAccepted = MutableLiveData<Boolean>().apply {
        value = sharedPrefs.hasAcceptedTerms
    }
    val isMenuVisible = MutableLiveData<Boolean>().apply { value = false }
    val isSourceFilterVisible = MutableLiveData<Event<Boolean>>().apply { value = Event(false) }
    val isFilterButtonVisible = MutableLiveData<Boolean>().apply { value = true }
    val isBookmarksShown = MutableLiveData<Boolean>().apply { value = false }
    val isBookmarksButtonVisible = MutableLiveData<Boolean>().apply { value = true }
    val isBookmarksButtonSelected = MutableLiveData<Boolean>().apply { value = false }


    val sourceUIModelData: LiveData<List<SourceUIModel>> =
        map(sourceRepo.getAll()) { sourceList ->
            sourceList.map { source ->
                SourceUIModel(source, sourceClickListener)
            }
        }

    private val sourceClickListener = object : UiModelClickListener<Source> {
        override fun onClick(source: Source) {
            source.isActive = !source.isActive

            launch(Dispatchers.IO) {
                /* update source when activated */
                if (source.isActive) {
                    postRepo.refresh(
                        this,
                        listOf(source)
                    )
                }

                sourceRepo.update(source)
            }
        }
    }

    fun onHomeNavSelected() {
        toolbarTitle.postValue(R.string.app_name)
        onNavigation.postValue(Destination.FEED)
        isFilterButtonVisible.postValue(!(isBookmarksButtonSelected.value ?: true))
        isBookmarksButtonVisible.postValue(true)
        isMenuVisible.postValue(false)
    }

    fun onNewsletterNavSelected() {
        toolbarTitle.postValue(R.string.nav_title_newsletter)
        onNavigation.postValue(Destination.NEWSLETTER)
        isFilterButtonVisible.postValue(false)
        isBookmarksButtonVisible.postValue(false)
        isMenuVisible.postValue(false)
    }

    fun onContributeNavSelected() {
        toolbarTitle.postValue(R.string.nav_title_contribute)
        onNavigation.postValue(Destination.CONTRIBUTE)
        isFilterButtonVisible.postValue(false)
        isBookmarksButtonVisible.postValue(false)
        isMenuVisible.postValue(false)
    }

    fun onConferencesNavSelected() {
        toolbarTitle.postValue(R.string.nav_title_conferences)
        onNavigation.postValue(Destination.CONFERENCES)
        isFilterButtonVisible.postValue(false)
        isBookmarksButtonVisible.postValue(false)
        isMenuVisible.postValue(false)
    }

    fun onAboutNavSelected() {
        toolbarTitle.postValue(R.string.nav_title_about)
        onNavigation.postValue(Destination.ABOUT)
        isFilterButtonVisible.postValue(false)
        isBookmarksButtonVisible.postValue(false)
        isMenuVisible.postValue(false)
    }

    fun onSourceFilterClicked() {
        isSourceFilterVisible.postValue(Event(true))
    }

    fun onMenuClicked() {
        val isCurrentlyVisible = isMenuVisible.value ?: false
        isMenuVisible.postValue(!isCurrentlyVisible)
        isBookmarksButtonVisible.postValue(!isCurrentlyVisible)
    }

    fun onBookmarksEvent() {
        val isCurrentlySelected = (isBookmarksButtonSelected.value ?: false)
        isBookmarksShown.postValue(!isCurrentlySelected)
        isBookmarksButtonSelected.postValue(!isCurrentlySelected)
    }

    fun onToolbarTitleClicked() {
        scrollTop.postValue(Event(Unit))
    }

    fun onCollapseMenu() {
        isMenuVisible.postValue(false)
    }

    fun onBackPressed(
        isFilterDrawerOpen: Boolean,
        navigateBack: () -> Unit
    ) {
        when {
            isMenuVisible.value == true -> isMenuVisible.postValue(false)
            isFilterDrawerOpen -> isSourceFilterVisible.postValue(Event(false))
            else -> navigateBack()
        }
    }

}