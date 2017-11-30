package com.droidfeed.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.droidfeed.data.model.Article

/**
 * Created by Dogan Gulcan on 9/30/17.
 */
@Dao
interface RssDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertArticles(rssItem: List<Article>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateArticle(article: Article)

    @Delete
    fun deleteArticle(article: Article)

    @Query("SELECT * FROM ${AppDatabase.RSS_TABLE_NAME}")
    fun getAllRss(): LiveData<List<Article>>

    @Query("SELECT * FROM ${AppDatabase.RSS_TABLE_NAME} WHERE bookmarked = 1")
    fun getBookmarkedArticles(): LiveData<List<Article>>

    @Query("SELECT COUNT(*) from ${AppDatabase.RSS_TABLE_NAME}")
    fun getFeedItemCount(): Int

}