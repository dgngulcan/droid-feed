package com.droidfeed.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.droidfeed.data.DataStatus
import com.droidfeed.data.db.PostDao
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.data.parser.NewsXmlParser
import com.droidfeed.ui.adapter.model.PostUIModel
import com.droidfeed.util.logThrowable
import kotlinx.coroutines.launch
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
) : BaseRepo() {

    /**
     * Returns all the posts as a [Listing].
     *
     * @param sources to fetch posts from
     * @param transform function for the models
     */
    fun getAllPosts(
        sources: LiveData<List<Source>>,
        transform: (List<Post>) -> List<PostUIModel>
    ): Listing<PostUIModel> {
        val pagedList = LivePagedListBuilder(
            postDao.getAllPosts().mapByPage {
                transform(it)
            },
            pagedListConfig
        ).build()

        val refreshTrigger = MutableLiveData<Unit>()
        val networkState = switchMap(refreshTrigger) { refresh<PostUIModel>(sources) }

        return Listing<PostUIModel>(
            pagedList = pagedList,
            networkState = networkState,
            refresh = {
                refreshTrigger.value = null
            },
            retry = {}
        )
    }

    fun updatePost(post: Post) = launch { postDao.updateArticle(post) }

    private fun <T> refresh(sources: LiveData<List<Source>>): LiveData<DataStatus<T>> {
        val networkState = MutableLiveData<DataStatus<T>>()

        launch {
            sources.value?.forEach { source ->
                val result = fetch(source)
                when (result) {
                    is DataStatus.Loading -> networkState.postValue(DataStatus.Loading())
                    is DataStatus.Successful -> {
                        result.data?.let { savePostsToDB(it) }
                        networkState.postValue(DataStatus.Successful())
                    }
                    is DataStatus.Failed -> {
                        networkState.postValue(DataStatus.Failed(result.throwable))
                    }
                    is DataStatus.HttpFailed -> {
                        networkState.postValue(DataStatus.HttpFailed(result.code))
                    }
                }
            }
        }

        return networkState
    }

    private fun fetch(source: Source): DataStatus<List<Post>> {
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

    fun getBookmarkedPosts(
        createUiModels: (List<Post>) -> List<PostUIModel>
    ): Listing<PostUIModel> {

        val pagedList = LivePagedListBuilder(
            postDao.getBookmarkedPosts()
                .mapByPage {
                    createUiModels(it)
                },
            pagedListConfig
        ).build()

        val dummyStatus = MutableLiveData<DataStatus<List<PostUIModel>>>()
        dummyStatus.postValue(DataStatus.Successful())

        return Listing<PostUIModel>(pagedList = pagedList,
            networkState = dummyStatus as LiveData<DataStatus<PostUIModel>>,
            refresh = {},
            retry = {}
        )
    }


    private fun savePostsToDB(posts: List<Post>) = launch {
        postDao.insertArticles(posts)
    }

    companion object {
        private const val PAGE_SIZE = 30
        private const val ENABLE_PLACEHOLDERS = false

        private val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(ENABLE_PLACEHOLDERS)
            .build()
    }
}