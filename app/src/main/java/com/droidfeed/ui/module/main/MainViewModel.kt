package com.droidfeed.ui.module.main

import android.content.SharedPreferences
import android.util.Patterns
import android.webkit.URLUtil
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import com.droidfeed.R
import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.model.SourceUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import com.droidfeed.util.hasAcceptedTerms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val sourceRepo: SourceRepo,
    private val postRepo: PostRepo,
    sharedPrefs: SharedPreferences
) : BaseViewModel() {

    val toolbarTitle = MutableLiveData<@StringRes Int>().apply { value = R.string.app_name }
    val onNavigation = MutableLiveData<Destination>().apply { value = Destination.FEED }
    val scrollTop = MutableLiveData<Event<Unit>>()

    val isUserTermsAccepted = MutableLiveData<Boolean>().apply {
        value = sharedPrefs.hasAcceptedTerms
    }
    val sourceAddIcon = MutableLiveData<@DrawableRes Int>().apply {
        value = R.drawable.avd_close_to_add
    }
    val isMenuVisible = MutableLiveData<Boolean>().apply { value = false }
    val isSourceInputVisible = MutableLiveData<Boolean>().apply { value = false }
    val isSourceFilterVisible = MutableLiveData<Event<Boolean>>().apply { value = Event(false) }
    val closeKeyboardEvent = MutableLiveData<Event<Boolean>>().apply { value = Event(false) }
    val sourceErrText = MutableLiveData<@StringRes Int>().apply { value = R.string.empty_string }
    val sourceInputText = MutableLiveData<@StringRes Int>().apply { value = R.string.empty_string }
    val isSourceProgressVisible = MutableLiveData<Boolean>().apply { value = false }
    val isSourceAddButtonEnabled = MutableLiveData<Boolean>().apply { value = true }
    val isFilterButtonVisible = MutableLiveData<Boolean>().apply { value = true }
    val isBookmarksShown = MutableLiveData<Boolean>().apply { value = false }
    val isBookmarksButtonVisible = MutableLiveData<Boolean>().apply { value = true }
    val isBookmarksButtonSelected = MutableLiveData<Boolean>().apply { value = false }
    val showUndoSourceRemoveSnack = MutableLiveData<Event<() -> Unit>>()
    val shareSourceEvent = MutableLiveData<Event<String>>()

    private var isUserAddedSourceExist = false

    val sourceUIModelData: LiveData<List<SourceUIModel>> = map(sourceRepo.getAll()) { sourceList ->
        isUserAddedSourceExist = !sourceList.none { it.isUserSource }

        sourceList.map { source ->
            SourceUIModel(
                source = source,
                onClick = this::onSourceClicked,
                onRemoveClick = this::onSourceRemoveClicked,
                onShareClick = this::onSourceShareClicked
            )
        }
    }

    private fun onSourceShareClicked(source: Source) {
        val content = "${source.name}\n ${source.url}"
        shareSourceEvent.postValue(Event(content))
        analytics.logSourceShare()
    }

    init {
        updateSources(sourceRepo)
    }

    private fun updateSources(sourceRepo: SourceRepo) {
        launch(Dispatchers.IO) {
            val result = sourceRepo.pull()
            if (result is DataStatus.Successful) {
                sourceRepo.insert(result.data ?: emptyList())
            }
        }
    }

    private fun onSourceRemoveClicked(source: Source) {
        launch(Dispatchers.IO) {
            sourceRepo.remove(source)
            isSourceAddButtonEnabled.postValue(true)
            analytics.logRemoveSourceButtonClick()

            showUndoSourceRemoveSnack.postValue(Event {
                addSource(source)
                analytics.logSourceRemoveUndo()
            })
        }
    }

    private fun addSource(source: Source) {
        launch(Dispatchers.IO) { sourceRepo.insert(source) }
    }

    private fun onSourceClicked(source: Source) {
        source.isActive = !source.isActive
        analytics.logSourceActivation(source.isActive)

        launch(Dispatchers.IO) {
            /* update source when activated */
            if (source.isActive) {
                postRepo.refresh(this, listOf(source))
            }

            sourceRepo.update(source)
        }
    }

    fun onHomeNavSelected() {
        toolbarTitle.postValue(R.string.app_name)
        onNavigation.postValue(Destination.FEED)
        isFilterButtonVisible.postValue(!(isBookmarksButtonSelected.value ?: true))
        isBookmarksButtonVisible.postValue(true)
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

    fun onAddSourceClicked() {
        val shouldOpenInputField = !(isSourceInputVisible.value!!)
        isSourceInputVisible.postValue(shouldOpenInputField)

        if (shouldOpenInputField) {
            R.drawable.avd_add_to_close
        } else {
            R.drawable.avd_close_to_add
        }.also(sourceAddIcon::postValue)

        analytics.logAddSourceButtonClick()
    }

    fun onSaveSourceClicked(url: String) {
        launch(Dispatchers.IO) {
            val trimmedUrl = url.trimIndent()
            if (Patterns.WEB_URL.matcher(trimmedUrl.toLowerCase()).matches()) {
                sourceErrText.postValue(R.string.empty_string)

                val cleanUrl = URLUtil.guessUrl(trimmedUrl)

                val alreadyExists = sourceRepo.isSourceExisting(cleanUrl)

                if (alreadyExists) {
                    sourceErrText.postValue(R.string.error_source_exists)
                    analytics.logSourceAlreadyExists()
                } else {
                    isSourceProgressVisible.postValue(true)
                    isSourceAddButtonEnabled.postValue(false)
                    closeKeyboardEvent.postValue(Event(true))

                    when (sourceRepo.addFromUrl(cleanUrl)) {
                        is DataStatus.Successful -> {
                            isSourceProgressVisible.postValue(false)
                            isSourceAddButtonEnabled.postValue(true)
                            isSourceInputVisible.postValue(false)
                            sourceAddIcon.postValue(R.drawable.avd_close_to_add)
                            sourceInputText.postValue(R.string.empty_string)
                            analytics.logSourceAddSuccess()
                        }
                        is DataStatus.Failed -> {
                            sourceErrText.postValue(R.string.error_add_source)
                            isSourceProgressVisible.postValue(false)
                            isSourceAddButtonEnabled.postValue(true)
                            analytics.logSourceAddFail()
                        }
                        is DataStatus.HttpFailed -> {
                            sourceErrText.postValue(R.string.error_internet_or_url)
                            isSourceProgressVisible.postValue(false)
                            isSourceAddButtonEnabled.postValue(true)
                        }
                    }

                }
            } else if (url.isEmpty()) {
                sourceErrText.postValue(R.string.error_empty_source_url)
            } else {
                sourceErrText.postValue(R.string.error_invalid_url)
                analytics.logSourceAddFailInvalidUrl()
            }

            analytics.logSaveSourceButtonClick()
        }

    }

    @Suppress("UNUSED_PARAMETER")
    fun onSourceInputTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (text.isNotEmpty()) {
            sourceErrText.postValue(R.string.empty_string)
        }
    }

    fun onMenuClicked() {
        val isCurrentlyVisible = isMenuVisible.value ?: false
        isMenuVisible.postValue(!isCurrentlyVisible)
    }

    fun onBookmarksEvent() {
        val isCurrentlySelected = (isBookmarksButtonSelected.value ?: false)
        isBookmarksShown.postValue(!isCurrentlySelected)
        isBookmarksButtonSelected.postValue(!isCurrentlySelected)
        isFilterButtonVisible.postValue(isCurrentlySelected)
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

    fun onFilterDrawerClosed() {
        isSourceInputVisible.postValue(false)
        sourceInputText.postValue(R.string.empty_string)
        sourceErrText.postValue(R.string.empty_string)
        sourceAddIcon.postValue(R.drawable.avd_close_to_add)
    }
}