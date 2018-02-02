package com.droidfeed.di

import dagger.Module

/**
 * Created by Dogan Gulcan on 9/22/17.
 */
@Module(includes = [
    (DatabaseModule::class),
    (ApiModule::class),
    (ViewModelModule::class)
])
class AppModule