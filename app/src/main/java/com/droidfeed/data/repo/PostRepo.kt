package com.droidfeed.data.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.Transformations.switchMap
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.droidfeed.data.DataResource
import com.droidfeed.data.DataStatus
import com.droidfeed.data.db.PostDao
import com.droidfeed.data.model.Post
import com.droidfeed.data.model.Source
import com.droidfeed.data.parser.NewsXmlParser
import com.droidfeed.ui.adapter.model.PostUIModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import okhttp3.*
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
    val rssXmlParser: NewsXmlParser,
    private val postDao: PostDao
) {

    fun getAllPosts(
        sources: LiveData<List<Source>>,
        createUiModels: (List<Post>) -> List<PostUIModel>
    ): Listing<PostUIModel> {
        val pagedList = LivePagedListBuilder(
            postDao.getAllPosts().mapByPage { it ->
                createUiModels(it)
            },
            pagedListConfig
        ).build()

        val refreshTrigger = MutableLiveData<Unit>()
        val networkState = Transformations.switchMap(refreshTrigger) { refresh(sources) }

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
        val articleResources = MediatorLiveData<DataResource<List<Post>>>()

        switchMap(sources) { it ->
            launch {
                it.map { source ->
                    fetch(source)
                }.forEach { it ->
                    articleResources.addSource(it) {
                        launch { it?.data?.let { posts -> savePostsToDB(posts) } }
                    }
                    articleResources.postValue(DataResource.success(it) as DataResource<List<Post>>)
                }
            }
            articleResources as LiveData<DataResource<List<Post>>>
        }

        return networkState
    }

    private fun fetch(source: Source): MutableLiveData<DataResource<List<Post>>> {
        val fetchResponse = MutableLiveData<DataResource<List<Post>?>>()
        val request = Request.Builder()
            .url(source.url)
            .build()

        okHttpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val posts =
                            response.body()?.string()?.let { rssXmlParser.parse(it, source) }
                        fetchResponse.postValue(DataResource.success(posts))
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    launch(UI) { fetchResponse.value = DataResource.error(e) }
                }
            })

        return fetchResponse as MutableLiveData<DataResource<List<Post>>>
    }

    fun getBookmarkedPosts(
        createUiModels: (List<Post>) -> List<PostUIModel>
    ): Listing<PostUIModel> {

        val pagedList = LivePagedListBuilder(
            postDao.getBookmarkedPosts().mapByPage { it ->
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