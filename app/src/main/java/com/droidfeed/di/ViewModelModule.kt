@file:Suppress("unused")

package com.droidfeed.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.droidfeed.ui.common.DFViewModelFactory
import com.droidfeed.ui.module.about.AboutViewModel
import com.droidfeed.ui.module.about.licence.LicencesViewModel
import com.droidfeed.ui.module.conferences.ConferencesViewModel
import com.droidfeed.ui.module.contribute.ContributeViewModel
import com.droidfeed.ui.module.feed.FeedViewModel
import com.droidfeed.ui.module.main.MainViewModel
import com.droidfeed.ui.module.onboard.OnBoardViewModel
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
    @ViewModelKey(ContributeViewModel::class)
    abstract fun bindContributeViewModel(viewModel: ContributeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    abstract fun bindAboutViewModel(viewModel: AboutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnBoardViewModel::class)
    abstract fun bindOnBoardViewModel(viewModel: OnBoardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LicencesViewModel::class)
    abstract fun bindLicencesViewModel(viewModel: LicencesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConferencesViewModel::class)
    abstract fun bindConferencesViewModel(viewModel: ConferencesViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DFViewModelFactory): ViewModelProvider.Factory
}
