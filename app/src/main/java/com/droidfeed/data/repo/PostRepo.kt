package com.droidfeed.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.droidfeed.data.DataResource
import com.droidfeed.data.DataStatus
import com.droidfeed.data.db.PostDao
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.data.parser.NewsXmlParser
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.util.logStackTrace
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that is responsible for operating posts.
 */
@Suppress("UNCHECKED_CAST")
@Singleton
class PostRepo @Inject constructor(
    private val okHttpClient: OkHttpClient,
   private val xmlParser: NewsXmlParser,
    private val postDao: PostDao
) {

    fun getAllPosts(
        sources: LiveData<List<Source>>,
        createUiModels: (List<Post>) -> List<PostUIModel>
    ): Listing<PostUIModel> {
        val pagedList = LivePagedListBuilder(
            postDao.getAllPosts().mapByPage {
                createUiModels(it)
            },
            pagedListConfig
        ).build()

        val refreshTrigger = MutableLiveData<Unit>()
        val networkState = switchMap(refreshTrigger) { refresh(sources) }

        refresh(sources)

        return Listing<PostUIModel>(
            pagedList = pagedList,
            networkState = networkState,
            refresh = {
                refreshTrigger.value = null
            },
            retry = {

            }
        )
    }

    fun updatePost(post: Post) = launch { postDao.updateArticle(post) }

    private fun refresh(sources: LiveData<List<Source>>): LiveData<DataStatus> {
        val networkState = MutableLiveData<DataStatus>()

        launch {
            sources.value?.forEach { source ->
                val result = fetch(source)

                result.data
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { savePostsToDB(it) }

                networkState.postValue(result.dataState)
            }
        }

        return networkState
    }

    private fun fetch(source: Source): DataResource<List<Post>> {
        val request = Request.Builder()
            .url(source.url)
            .build()

        return try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val posts = response.body()?.string()?.let {
                    xmlParser.parse(it, source)
                }

                DataResource.success(posts)
            } else {
                DataResource.success(emptyList())
            }
        } catch (e: IOException) {
            logStackTrace(e)
            DataResource.error(e)
        }
    }

    fun getBookmarkedPosts(
        createUiModels: (List<Post>) -> List<PostUIModel>
    ): Listing<PostUIModel> {

        val pagedList = LivePagedListBuilder(
            postDao.getBookmarkedPosts().mapByPage {
                createUiModels(it)
            },
            pagedListConfig
        ).build()

        val dummyStatus = MutableLiveData<DataStatus>()
        dummyStatus.postValue(DataStatus.Success)

        return Listing<PostUIModel>(pagedList = pagedList,
            networkState = dummyStatus,
            refresh = {},
            retry = {}
        )
    }

    private fun savePostsToDB(posts: List<Post>) = launch { postDao.insertArticles(posts) }

    companion object {
        private const val PAGE_SIZE = 20
        private const val ENABLE_PLACEHOLDERS = true

        private val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setEnablePlaceholders(ENABLE_PLACEHOLDERS)
            .build()
    }
}