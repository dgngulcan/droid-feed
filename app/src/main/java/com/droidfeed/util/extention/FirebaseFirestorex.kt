package com.droidfeed.util.extention

import com.google.firebase.firestore.FirebaseFirestore

fun FirebaseFirestore.getAllOperativeSources() = collection("sources")
    .whereEqualTo(
        "is_operative",
        true
    )
    .get()

fun FirebaseFirestore.getUpcomingConferences() = collection("conferences")
    .orderBy("date_start")
//    .whereGreaterThanOrEqualTo(
//        "date_start",
//        System.currentTimeMillis()
//    )
    .get()