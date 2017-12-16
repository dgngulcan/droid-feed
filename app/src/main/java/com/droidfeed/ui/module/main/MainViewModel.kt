package com.droidfeed.ui.module.main

import android.arch.lifecycle.MutableLiveData
import com.droidfeed.R
import com.nytclient.ui.common.BaseViewModel

/**
 * Created by Dogan Gulcan on 11/9/17.
 */
class MainViewModel : BaseViewModel() {

    val navigationHeaderImage = MutableLiveData<Int>()

    init {
//        navigationHeaderImage.value=
    }

    fun shuffleHeaderImage(){
        navigationHeaderImage

    }

//    private val headerImageResources =
//            listOf(R.drawable.ic_cat,
//                    R.drawable.ic_cloud_rain,
//                    R.drawable.ic_code,
//                    R.drawable.ic_coffee,
//                    R.drawable.ic_droid,
//                    R.drawable.ic_dog,
//                    R.drawable.ic_floppy,
//                    R.drawable.ic_keyboard,
//                    R.drawable.ic_merge,
//                    R.drawable.ic_office_desk,
//                    R.drawable.ic_plant,
//                    R.drawable.ic_tea)


}