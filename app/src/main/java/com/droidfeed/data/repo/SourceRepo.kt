package com.droidfeed.data.repo

import com.droidfeed.data.DataStatus
import com.droidfeed.data.db.SourceDao
import com.droidfeed.data.model.Source
import com.droidfeed.data.parser.NewsXmlParser
import com.droidfeed.util.extension.isOnline
import com.droidfeed.util.logThrowable
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Repository of feed sources.
 */
@Singleton
class SourceRepo @Inject constructor(
    private val sourceDao: SourceDao,
    private val firestore: FirebaseFirestore,
    private val okHttpClient: OkHttpClient,
    private val xmlParser: NewsXmlParser
) {

    fun getAll() = sourceDao.getSources()

    fun getActives() = sourceDao.getActiveSources()

    fun getActiveCount() = sourceDao.getActiveSourceCount()

    /**
     * Updates a source in the DB.
     *
     * @param source
     */
    fun update(source: Source) = sourceDao.updateSource(source)

    fun insert(sources: List<Source>) = sourceDao.insertSources(sources)

    fun insert(sources: Source) = sourceDao.insertSource(sources)

    /**
     * Adds news source from given url.
     *
     * @param sourceUrl valid RSS or Atom feed url
     */
    fun addFromUrl(sourceUrl: String): DataStatus<String> {
        return try {
            val request = Request.Builder()
                .url(sourceUrl)
                .build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val channelTitle = response.body?.string()?.let { feedString ->
                    xmlParser.getChannelTitle(feedString)
                }

                if (channelTitle == null) {
                    DataStatus.Failed()
                } else {
                    val source = Source(
                        id = 0, /* will be auto-set by room */
                        name = channelTitle,
                        url = sourceUrl,
                        isUserSource = true
                    )

                    sourceDao.insertSource(source)
                    DataStatus.Successful(channelTitle)
                }
            } else {
                DataStatus.HttpFailed(response.code)
            }
        } catch (e: IOException) {
            logThrowable(e)
            DataStatus.Failed(e)
        }
    }

    /**
     * Pulls sources from [FirebaseFirestore].
     */
    suspend fun pull() = suspendCoroutine<DataStatus<List<Source>>> { continuation ->
        firestore.collection("sources")
            .whereEqualTo(
                "is_operational",
                true
            )
            .get()
            .addOnSuccessListener { result ->
                val sources = result.documents.map { document ->
                    Source(
                        (document["id"] as Long).toInt(),
                        document["name"] as String,
                        document["url"] as String,
                        false
                    )
                }

                if (sources.isEmpty() &&
                    !FirebaseFirestore.getInstance().app.applicationContext.isOnline()
                ) {
                    continuation.resume(DataStatus.Failed(UnknownHostException()))
                } else {
                    continuation.resume(DataStatus.Successful(sources))
                }
            }.addOnFailureListener { exception ->
                continuation.resume(DataStatus.Failed(exception))
            }
    }

    fun isSourceExisting(sourceUrl: String) = sourceDao.isUrlExists(sourceUrl).isNotEmpty()

    fun remove(source: Source) = sourceDao.remove(source)

}