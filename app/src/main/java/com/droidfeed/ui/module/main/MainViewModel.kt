package com.droidfeed.ui.module.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.droidfeed.R
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.model.SourceUiModel
import com.droidfeed.ui.common.BaseViewModel
import com.droidfeed.util.extention.random
import javax.inject.Inject

class MainViewModel @Inject constructor(
    sourceRepo: SourceRepo
) : BaseViewModel() {

    private val result = MutableLiveData<List<SourceUiModel>>()

    val navigationHeaderImage = MutableLiveData<Int>()

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

    /**
     * The drawable id's of the icons displayed on top of the navigation drawer.
     */
    private val drawerImageIds =
        listOf(
            R.drawable.ic_cat,
            R.drawable.ic_cloud_rain,
            R.drawable.ic_code,
            R.drawable.ic_coffee,
            R.drawable.ic_droid,
            R.drawable.ic_dog,
            R.drawable.ic_floppy,
            R.drawable.ic_keyboard,
            R.drawable.ic_merge,
            R.drawable.ic_office_desk,
//            R.drawable.ic_plant,
            R.drawable.ic_tea
        )

    init {
        shuffleHeaderImage()
    }

    fun shuffleHeaderImage() {
        navigationHeaderImage.value = drawerImageIds[(0 until drawerImageIds.size).random()]
    }
}