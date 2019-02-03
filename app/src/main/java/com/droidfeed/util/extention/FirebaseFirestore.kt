package com.droidfeed.util.extention

import com.google.firebase.firestore.FirebaseFirestore

fun FirebaseFirestore.getSources() = collection("sources").get()