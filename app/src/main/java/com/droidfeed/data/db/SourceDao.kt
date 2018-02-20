package com.droidfeed.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.droidfeed.data.db.AppDatabase.Companion.SOURCE_TABLE_NAME
import com.droidfeed.data.model.Source

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Dao
interface SourceDao {

    @Query(
        "SELECT * FROM $SOURCE_TABLE_NAME " +
                "ORDER BY name ASC"
    )
    fun getSources(): LiveData<List<Source>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSources(source: List<Source>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSource(source: Source)

}