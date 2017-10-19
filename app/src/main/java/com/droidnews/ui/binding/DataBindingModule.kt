package com.droidnews.ui.binding

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 10/3/17.
 */
@Module
class DataBindingModule {

    @Singleton
    @Provides
    fun provideDateTimeBindingComponent(): DateTimeDataBindingComponent {
        return DateTimeDataBindingComponent()
    }

}