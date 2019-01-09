package com.droidfeed.data.repo

import com.droidfeed.data.db.SourceDao
import com.droidfeed.data.model.Source
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Repository of feed sources.
 */
@Singleton
class SourceRepo @Inject constructor(
    private val sourceDao: SourceDao
) {

    val sources by lazy { sourceDao.getSources() }

    /**
     * Updates a source in the DB.
     *
     * @param source
     */
    fun updateSource(source: Source) = GlobalScope.launch(Dispatchers.IO) {
        sourceDao.updateSource(source)
    }
}