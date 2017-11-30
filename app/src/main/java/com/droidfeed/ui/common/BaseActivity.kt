package com.nytclient.ui.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


/**
 * Created by Dogan Gulcan on 9/12/17.
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        initViewModel()
        createBindings()
        bindBindings()
    }

    abstract fun initViewModel()
    abstract fun createBindings()
    abstract fun bindBindings()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector
}
