package com.nytclient.ui.common

import android.content.Context
import android.support.v4.app.Fragment
import dagger.android.support.AndroidSupportInjection


/**
 * Created by Dogan Gulcan on 9/13/17.
 */
abstract class BaseFragment : Fragment() {

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

}