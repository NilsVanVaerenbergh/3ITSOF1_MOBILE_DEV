package edu.ap.rentalapp.extensions.instances

import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.extensions.UserService
import android.content.Context

object UserServiceSingleton {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    fun getInstance(context: Context): UserService {
        return UserService(firestore, context)
    }
}