package com.droidfeed.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.droidfeed.data.model.Article

@Dao
interface RssDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertArticles(rssItem: List<Article>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateArticle(article: Article)

    @Delete
    fun deleteArticle(article: Article)

    @Query(
        "SELECT * FROM ${AppDatabase.RSS_TABLE_NAME} " +
            "ORDER BY pub_date_timestamp DESC"
    )
    fun getAllRss(): LiveData<List<Article>>

    @Query(
        "SELECT * FROM ${AppDatabase.RSS_TABLE_NAME} " +
            "WHERE bookmarked = 1 " +
            "ORDER BY pub_date_timestamp DESC"
    )
    fun getBookmarkedArticles(): LiveData<List<Article>>

    @Query("SELECT COUNT(*) FROM ${AppDatabase.RSS_TABLE_NAME}")
    fun getFeedItemCount(): Int

    @Query(
        "SELECT COUNT(*) FROM ${AppDatabase.RSS_TABLE_NAME} " +
            "WHERE bookmarked = 1"
    )
    fun getBookmarkedItemCount(): Int

    @Query(
        "DELETE FROM ${AppDatabase.RSS_TABLE_NAME} " +
            "WHERE bookmarked = 0 " +
            "IN (SELECT pub_date_timestamp from ${AppDatabase.RSS_TABLE_NAME} " +
            "ORDER BY pub_date_timestamp DESC LIMIT 10)"
    )
    fun trimCache()

    @Query(
        "DELETE FROM ${AppDatabase.RSS_TABLE_NAME} " +
            "WHERE bookmarked == 0 " +
            "AND channel_title LIKE :sourceUrl || '%'"
    )
    fun clearNonBookmarkedSource(sourceUrl: String)
}