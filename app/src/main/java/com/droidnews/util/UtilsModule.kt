package com.droidnews.util

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 9/23/17.
 */
@Module
class UtilsModule {

    @Singleton
    @Provides
    fun providesDateTimeUtils(): DateTimeUtils {
        return DateTimeUtils()
    }
}