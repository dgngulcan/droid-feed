package com.droidfeed.di

import android.arch.lifecycle.ViewModel
import com.droidfeed.ui.module.feed.FeedViewModel
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.ui.module.newsletter.NewsletterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


/**
 * Created by Dogan Gulcan on 1/23/18.
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel::class)
    abstract fun bindFeedViewModel(feedViewModel: FeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsletterViewModel::class)
    abstract fun bindNewsLetterViewModel(newsletterViewModel: NewsletterViewModel): ViewModel

}
