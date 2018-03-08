package com.droidfeed.ui.module.contribute

import com.droidfeed.ui.common.SingleLiveEvent
import com.droidfeed.ui.common.BaseViewModel

/**
 * Created by Dogan Gulcan on 12/16/17.
 */
class ContributeViewModel : BaseViewModel() {

    val contactDevEvent = SingleLiveEvent<String>()

    val onClickListener = object : ContributeClickListener {
        override fun onSendFeedbackClicked() {
            contactDevEvent.setValue("https://github.com/dgngulcan/droid-feed")
        }
    }

}