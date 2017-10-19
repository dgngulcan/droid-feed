package com.droidnews.ui.binding;

/**
 * Created by Dogan Gulcan on 10/3/17.
 */
public class DateTimeDataBindingComponent implements android.databinding.DataBindingComponent {

    @Override
    public DateTimeBindingAdapters getDateTimeBindingAdapters() {
        return new DateTimeBindingAdapters();
    }
}
