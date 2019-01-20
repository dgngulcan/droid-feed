package com.droidfeed.data.repo

import com.droidfeed.data.db.SourceDao
import com.droidfeed.data.model.Source
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository of feed sources.
 */
@Singleton
class SourceRepo @Inject constructor(
    private val sourceDao: SourceDao
) {

    fun getSources() = sourceDao.getSources()

    fun getActiveSources() = sourceDao.getActiveSources()

    fun getActiveSourceCount() = sourceDao.getActiveSourceCount()

    /**
     * Updates a source in the DB.
     *
     * @param source
     */
    fun updateSource(source: Source) {
        sourceDao.updateSource(source)
    }
}