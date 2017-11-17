package com.droidfeed.ui.module.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.droidfeed.R
import com.nytclient.ui.common.BaseViewModel
import java.util.*

/**
 * Created by Dogan Gulcan on 11/9/17.
 */
class MainViewModel : BaseViewModel() {

    val navigationHeaderImage: LiveData<Int> by lazy {
        return@lazy getNavigationHeaderImage()
    }

    private fun getNavigationHeaderImage(): MutableLiveData<Int> {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
        val liveData = MutableLiveData<Int>()

        if (timeOfDay in 6..19) {
            liveData.value = R.drawable.img_city_morning
        } else {
            liveData.value = R.drawable.img_city_night
        }
        return liveData
    }

}