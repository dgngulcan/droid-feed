package com.droidfeed.data.repo

import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Conference
import com.droidfeed.util.extension.isOnline
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ConferenceRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getUpcoming() = suspendCoroutine<DataStatus<List<Conference>>> { continuation ->
        firestore.collection("conferences")
            .orderBy(
                "date_start",
                Query.Direction.ASCENDING
            )
            .whereGreaterThanOrEqualTo(
                "date_start",
                Timestamp(Date())
            )
            .get()
            .addOnSuccessListener { result ->
                val conferences = result.documents.map { document ->
                    Conference(
                        document["name"] as String,
                        document["location"] as String,
                        document["url"] as String,
                        (document["date_start"] as Timestamp).toDate(),
                        (document["date_end"] as Timestamp).toDate(),
                        (document["cfp_start"] as? Timestamp)?.toDate(),
                        (document["cfp_end"] as? Timestamp)?.toDate(),
                        document["cfp_url"] as? String
                    )
                }

                if (conferences.isEmpty() &&
                    !FirebaseFirestore.getInstance().app.applicationContext.isOnline()
                ) {
                    continuation.resume(DataStatus.Failed(UnknownHostException()))
                } else {
                    continuation.resume(DataStatus.Successful(conferences))
                }
            }.addOnFailureListener { exception ->
                continuation.resume(DataStatus.Failed(exception))
            }
    }
}