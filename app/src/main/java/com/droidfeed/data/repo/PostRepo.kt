package com.droidfeed.data.repo

import com.droidfeed.data.DataStatus
import com.droidfeed.data.db.PostDao
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.data.parser.NewsXmlParser
import com.droidfeed.util.logThrowable
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST")
@Singleton
class PostRepo @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val xmlParser: NewsXmlParser,
    private val postDao: PostDao
) {

    fun getAllPosts() = postDao.getAllPosts()

    fun getBookmarkedPosts() = postDao.getBookmarkedPosts()

    fun updatePost(post: Post) = postDao.updateArticle(post)

    suspend fun refresh(sources: List<Source>): List<DataStatus<Nothing>> =
        suspendCoroutine { continuation ->
            val result = sources.map { source ->
                val result = fetchAndParsePosts(source)

                val status: DataStatus<Nothing> = when (result) {
                    is DataStatus.Loading -> {
                        DataStatus.Loading()
                    }
                    is DataStatus.Successful -> {
                        result.data?.let { addPosts(it) }
                        DataStatus.Successful()
                    }
                    is DataStatus.Failed -> {
                        DataStatus.Failed(result.throwable)
                    }
                    is DataStatus.HttpFailed -> {
                        DataStatus.HttpFailed(result.code)
                    }
                }
                status
            }

            continuation.resume(result)
        }

    private fun fetchAndParsePosts(source: Source): DataStatus<List<Post>> {
        val request = Request.Builder()
            .url(source.url)
            .build()

        return try {
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val posts = response.body()?.string()?.let {
                    xmlParser.parse(it, source)
                }

                DataStatus.Successful(posts)
            } else {
                DataStatus.HttpFailed(response.code())
            }

        } catch (e: IOException) {
            logThrowable(e)
            DataStatus.Failed(e)
        }
    }

    private fun addPosts(posts: List<Post>) = postDao.insertArticles(posts)

}