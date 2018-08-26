package com.droidfeed.ui.adapter

// TODO refactor to function
interface UiModelClickListener<in T> {

    fun onClick(model: T)
}