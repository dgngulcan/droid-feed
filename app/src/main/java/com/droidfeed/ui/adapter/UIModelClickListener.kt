package com.droidfeed.ui.adapter

interface UIModelClickListener<in T> {
    fun onClick(model: T)
}