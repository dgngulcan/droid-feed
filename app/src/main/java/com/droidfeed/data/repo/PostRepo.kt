package com.droidfeed.data.repo

import com.droidfeed.data.DataStatus
import com.droidfeed.data.db.PostDao
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.data.parser.NewsXmlParser
import com.droidfeed.util.logThrowable
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class PostRepo @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val xmlParser: NewsXmlParser,
    private val postDao: PostDao
) {

    fun getAll() = postDao.getAll()

    fun getBookmarked() = postDao.getBookmarked()

    fun getBookmarkedCount() = postDao.getBookmarkedCount()

    fun updatePost(post: Post) = postDao.update(post)

    private fun addPosts(posts: List<Post>) = postDao.insert(posts)

    /**
     * Refresh the posts of the given sources. Sources are fetched and parsed asynchronously. After
     * fetching and parsing, the posts are added to the database.
     *
     * @param coroutineScope
     * @param sources to refresh
     */
    suspend fun refresh(
        coroutineScope: CoroutineScope,
        sources: List<Source>
    ): List<DataStatus<Nothing>> {
        val allPosts = mutableListOf<Post>()

        val asyncFetches = sources.map { source ->
            coroutineScope.async(Dispatchers.IO) {
                when (val result = fetchAndParsePosts(source)) {
                    is DataStatus.Successful -> {
                        result.data?.let { allPosts.addAll(it) }
                        DataStatus.Successful()
                    }
                    else -> result as DataStatus<Nothing>
                }
            }
        }

        val results = asyncFetches.awaitAll()
        addPosts(allPosts)

        return results
    }

    private fun fetchAndParsePosts(source: Source): DataStatus<List<Post>> {
        val request = Request.Builder()
            .url(source.url)
            .build()

        return try {
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val posts = response.body?.string()?.let {
                    xmlParser.parse(it, source)
                }

                DataStatus.Successful(posts)
            } else {
                DataStatus.HttpFailed(response.code)
            }
        } catch (e: IOException) {
            logThrowable(e)
            DataStatus.Failed(e)
        }
    }
}