package com.droidfeed.data.repo

import com.droidfeed.data.db.SourceDao
import com.droidfeed.data.model.Source
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dogan Gulcan on 2/12/18.
 */
@Singleton
class SourceRepo @Inject constructor(
    private val sourceDao: SourceDao
) {

    val sources by lazy { sourceDao.getSources() }

    fun updateSource(source: Source) {
        launch {
            sourceDao.updateSource(source)
        }
    }

}