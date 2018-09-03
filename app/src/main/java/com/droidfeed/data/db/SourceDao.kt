package com.droidfeed.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.droidfeed.data.db.AppDatabase.Companion.SOURCE_TABLE_NAME
import com.droidfeed.data.model.Source

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