package com.droidfeed.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.droidfeed.ui.common.DFViewModelFactory
import com.droidfeed.ui.module.about.AboutViewModel
import com.droidfeed.ui.module.contribute.ContributeViewModel
import com.droidfeed.ui.module.feed.FeedViewModel
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.ui.module.newsletter.NewsletterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel::class)
    abstract fun bindFeedViewModel(viewModel: FeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsletterViewModel::class)
    abstract fun bindNewsLetterViewModel(viewModel: NewsletterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContributeViewModel::class)
    abstract fun bindContributeViewModel(viewModel: ContributeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    abstract fun bindAboutViewModel(viewModel: AboutViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DFViewModelFactory): ViewModelProvider.Factory
}
