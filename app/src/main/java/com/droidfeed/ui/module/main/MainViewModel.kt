package com.droidfeed.ui.module.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.model.SourceUiModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import javax.inject.Inject

class MainViewModel @Inject constructor(
    sourceRepo: SourceRepo
) : BaseViewModel() {

    private val result = MutableLiveData<List<SourceUiModel>>()
    val bookmarksEvent = MutableLiveData<Event<Boolean>>()
    val hideMenuEvent = MutableLiveData<Event<Unit>>()

    val sourceUiModelData: LiveData<List<SourceUiModel>> =
        Transformations.switchMap(sourceRepo.sources) { sourceList ->
            result.value = sourceList.map {
                SourceUiModel(it, sourceClickListener)
            }
            result
        }

    private val sourceClickListener = object : UiModelClickListener<Source> {
        override fun onClick(model: Source) {
            model.isActive = !model.isActive
            sourceRepo.updateSource(model)
        }
    }

    fun onBookmarksEvent(isEnabled: Boolean) {
        bookmarksEvent.value = Event(isEnabled)
    }

    fun onScrolledEnough() {
        hideMenuEvent.postValue(Event(Unit))
    }
}