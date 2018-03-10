package com.droidfeed.ui.module.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.droidfeed.R
import com.droidfeed.data.model.Source
import com.droidfeed.data.repo.RssRepo
import com.droidfeed.data.repo.SourceRepo
import com.droidfeed.ui.adapter.UiModelClickListener
import com.droidfeed.ui.adapter.model.SourceUiModel
import com.droidfeed.util.extention.random
import com.droidfeed.ui.common.BaseViewModel
import javax.inject.Inject

/**
 * Created by Dogan Gulcan on 11/9/17.
 */
class MainViewModel @Inject constructor(
    sourceRepo: SourceRepo,
    rssRepo: RssRepo
) : BaseViewModel() {

    val navigationHeaderImage = MutableLiveData<Int>()

    //    private val sources by lazy { }
    private val result = MutableLiveData<List<SourceUiModel>>()

    val sourceUiModelData: LiveData<List<SourceUiModel>> =
        Transformations.switchMap(
            sourceRepo.sources,
            { sourceList ->
                result.value = sourceList.map {
                    SourceUiModel(it, sourceClickListener)
                }
                result
            })

    private val sourceClickListener = object : UiModelClickListener<Source> {
        override fun onClick(model: Source) {
            model.isActive = !model.isActive

            sourceRepo.updateSource(model)

            if (!model.isActive) {
                rssRepo.clearSource(model)
            }
        }
    }

    private val headerImageResources =
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
            R.drawable.ic_plant,
            R.drawable.ic_tea
        )

    init {
        shuffleHeaderImage()
    }

    fun shuffleHeaderImage() {
        navigationHeaderImage.value =
                headerImageResources[(0 until headerImageResources.size).random()]
    }

}