package com.droidfeed.data.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.droidfeed.data.model.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(posts: List<Post>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(post: Post)

    @Delete
    fun delete(post: Post)

    @Query(
        "SELECT * FROM ${AppDatabase.POST_TABLE} WHERE source_id " +
                "IN (SELECT  ${AppDatabase.SOURCE_TABLE}.id " +
                "FROM ${AppDatabase.SOURCE_TABLE} " +
                "WHERE is_active = 1) " +
                "ORDER BY pub_date_timestamp DESC"
    )
    fun getAll(): DataSource.Factory<Int, Post>

    @Query(
        "SELECT * FROM ${AppDatabase.POST_TABLE} " +
                "ORDER BY pub_date_timestamp DESC"
    )
    fun getAllAsLiveData(): LiveData<List<Post>>

    @Query(
        "SELECT * FROM ${AppDatabase.POST_TABLE} " +
                "WHERE bookmarked = 1 " +
                "ORDER BY pub_date_timestamp DESC"
    )
    fun getBookmarked(): DataSource.Factory<Int, Post>

    @Query("SELECT COUNT(*) FROM ${AppDatabase.POST_TABLE}")
    fun getPostCount(): Int

    @Query(
        "SELECT COUNT(*) FROM ${AppDatabase.POST_TABLE} " +
                "WHERE bookmarked = 1"
    )
    fun getBookmarkedCount(): Int

    @Query(
        "DELETE FROM ${AppDatabase.POST_TABLE} " +
                "WHERE bookmarked == 0 " +
                "AND channel_title LIKE :sourceUrl || '%'"
    )
    fun clearNonBookmarked(sourceUrl: String)
}