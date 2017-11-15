package com.droidfeed.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.droidfeed.data.model.Article

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Dao
interface RssDao {

    /**
     * @Insert a list of RSS items to the database. Replaces if the item exists.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRss(rssItem: List<Article>)

    /**
     * @return all RSS items in the database.
     */
    @Query("SELECT * FROM ${AppDatabase.RSS_TABLE_NAME}")
    fun getAllRss(): LiveData<List<Article>>

    @Query("SELECT COUNT(*) from ${AppDatabase.RSS_TABLE_NAME}")
    fun getFeedItemCount(): Int

    @Query("DELETE FROM ${AppDatabase.RSS_TABLE_NAME}")
    fun flushRssCache() {


    }

}