package com.droidfeed.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
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