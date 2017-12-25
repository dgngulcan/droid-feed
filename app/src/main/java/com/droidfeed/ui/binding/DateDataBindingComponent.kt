package com.droidfeed.ui.binding

import android.databinding.DataBindingComponent
import com.droidfeed.util.DateTimeUtils
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 11/3/17.
 */
@Singleton
class DateDataBindingComponent : DataBindingComponent {

    override fun getDateBindingAdapters(): DateBindingAdapters {
        return DateBindingAdapters()

    }

}