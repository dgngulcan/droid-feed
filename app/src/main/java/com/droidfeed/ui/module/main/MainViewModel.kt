package com.droidfeed.ui.module.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.PostRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.model.SourceUIModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.event.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    sourceRepo: SourceRepo,
    postRepo: PostRepo
) : BaseViewModel() {

    val bookmarksEvent = MutableLiveData<Event<Boolean>>()
    val hideMenuEvent = MutableLiveData<Event<Unit>>()

    val sourceUIModelData: LiveData<List<SourceUIModel>> =
        map(sourceRepo.getSources()) { sourceList ->
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
                    postRepo.refresh(listOf(source))
                }
                sourceRepo.updateSource(source)
            }
        }
    }

    fun onBookmarksEvent(isEnabled: Boolean) {
        bookmarksEvent.value = Event(isEnabled)
    }

    fun onScrolledEnough() {
        hideMenuEvent.postValue(Event(Unit))
    }
}