package com.ajo.abarrotesOsorio.data

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {

    val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}
