package com.ajo.abarrotesOsorio.data

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Objeto Singleton para proporcionar una única instancia de FirebaseFirestore.
 *
 * Utilizar un objeto (`object`) en Kotlin es la forma más simple y segura de
 * implementar el patrón Singleton. El acceso a la instancia de Firestore
 * se maneja de forma centralizada.
 */
object FirestoreHelper {

    /**
     * Propiedad que contiene la instancia de Firestore.
     * La instancia se inicializa solo una vez cuando se accede por primera vez.
     */
    val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}
