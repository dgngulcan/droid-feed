package com.droidfeed.util.extention

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

@Suppress("UNCHECKED_CAST")
fun <I, R, M, L : List<R>, T : List<M>> DataSource.Factory<I, R>.asLiveData(
    config: PagedList.Config,
    transform: (L) -> T
): LiveData<PagedList<M>> {
    return LivePagedListBuilder(
        mapByPage { values ->
            transform(values as L)
        },
        config
    ).build()
}