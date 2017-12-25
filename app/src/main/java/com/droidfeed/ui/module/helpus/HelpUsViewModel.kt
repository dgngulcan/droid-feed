package com.droidfeed.ui.module.helpus

import android.content.Intent
import com.droidfeed.util.contactIntent
import com.nytclient.ui.common.BaseViewModel
import com.droidfeed.ui.common.SingleLiveEvent

/**
 * Created by Dogan Gulcan on 12/16/17.
 */
class HelpUsViewModel : BaseViewModel() {

    val contactDevEvent = SingleLiveEvent<Intent>()

    val onClickListener = object : HelpUsClickListener {
        override fun onSendFeedbackClicked() {
            contactDevEvent.setValue(contactIntent)
        }
    }

}