package edu.ap.rentalapp.extensions.instances

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseStoreInstance {
    val instance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}