package com.droidfeed.ui.adapter

interface UiModelClickListener<in T> {
    fun onClick(model: T)
}