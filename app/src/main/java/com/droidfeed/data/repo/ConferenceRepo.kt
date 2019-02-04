package com.droidfeed.data.repo

import com.droidfeed.data.DataStatus
import com.droidfeed.data.model.Conference
import com.droidfeed.util.extention.getUpcomingConferences
import com.droidfeed.util.extention.isOnline
import com.google.firebase.firestore.FirebaseFirestore
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConferenceRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getUpcoming() = suspendCoroutine<DataStatus<List<Conference>>> { continuation ->
        firestore.getUpcomingConferences()
            .addOnSuccessListener { result ->
                val conferences = result.documents.map { document ->
                    Conference(
                        document["name"] as String,
                        document["location"] as String,
                        document["url"] as String,
                        document["date_start"] as Date,
                        document["date_end"] as Date,
                        document["cfp_start"] as? Date,
                        document["cfp_end"] as? Date,
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