package com.droidfeed.data.db

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.droidfeed.data.model.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertArticles(rssItem: List<Post>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateArticle(article: Post)

    @Delete
    fun deleteArticle(article: Post)

    @Query(
        "SELECT * FROM ${AppDatabase.POST_TABLE_NAME} " +
            "ORDER BY pub_date_timestamp DESC"
    )
    fun getAllPosts(): DataSource.Factory<Int, Post>

    @Query(
        "SELECT * FROM ${AppDatabase.POST_TABLE_NAME} " +
            "ORDER BY pub_date_timestamp DESC"
    )
    fun getAllPostsLiveData(): LiveData<List<Post>>

    @Query(
        "SELECT * FROM ${AppDatabase.POST_TABLE_NAME} " +
            "WHERE bookmarked = 1 " +
            "ORDER BY pub_date_timestamp DESC"
    )
    fun getBookmarkedArticles(): LiveData<List<Post>>

    @Query("SELECT COUNT(*) FROM ${AppDatabase.POST_TABLE_NAME}")
    fun getFeedItemCount(): Int

    @Query(
        "SELECT COUNT(*) FROM ${AppDatabase.POST_TABLE_NAME} " +
            "WHERE bookmarked = 1"
    )
    fun getBookmarkedItemCount(): Int

    @Query(
        "DELETE FROM ${AppDatabase.POST_TABLE_NAME} " +
            "WHERE bookmarked = 0 " +
            "IN (SELECT pub_date_timestamp from ${AppDatabase.POST_TABLE_NAME} " +
            "ORDER BY pub_date_timestamp DESC LIMIT 10)"
    )
    fun trimCache()

    @Query(
        "DELETE FROM ${AppDatabase.POST_TABLE_NAME} " +
            "WHERE bookmarked == 0 " +
            "AND channel_title LIKE :sourceUrl || '%'"
    )
    fun clearNonBookmarkedSource(sourceUrl: String)
}