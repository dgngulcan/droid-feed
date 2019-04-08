package com.droidfeed.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.droidfeed.data.db.AppDatabase.Companion.SOURCE_TABLE
import com.droidfeed.data.model.Source

@Dao
interface SourceDao {

    @Query("SELECT * FROM $SOURCE_TABLE ORDER BY name ASC")
    fun getSources(): LiveData<List<Source>>

    @Query("SELECT * FROM $SOURCE_TABLE WHERE is_active = 1")
    fun getActiveSources(): List<Source>

    @Query("SELECT * FROM $SOURCE_TABLE WHERE url = :sourceUrl LIMIT 1")
    fun isUrlExists(sourceUrl: String): List<Source>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSources(source: List<Source>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSource(source: Source)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSource(source: Source)

    @Query("SELECT COUNT(*) FROM ${AppDatabase.SOURCE_TABLE} WHERE is_active = 1")
    fun getActiveSourceCount(): Int
}