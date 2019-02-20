package com.droidfeed.data.repo

import androidx.annotation.WorkerThread
import com.droidfeed.data.DataStatus
import com.droidfeed.data.db.SourceDao
import com.droidfeed.data.model.Source
import com.droidfeed.util.extention.isOnline
import com.google.firebase.firestore.FirebaseFirestore
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
    private val firestore: FirebaseFirestore
) {

    fun getAll() = sourceDao.getSources()

    fun getActives() = sourceDao.getActiveSources()

    fun getActiveCount() = sourceDao.getActiveSourceCount()

    /**
     * Updates a source in the DB.
     *
     * @param source
     */
    @WorkerThread
    fun update(source: Source) = sourceDao.updateSource(source)


    @WorkerThread
    fun insert(sources: List<Source>) = sourceDao.insertSources(sources)


    /**
     * Pulls sources from Firebase Firestore.
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
                        document["url"] as String
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
}