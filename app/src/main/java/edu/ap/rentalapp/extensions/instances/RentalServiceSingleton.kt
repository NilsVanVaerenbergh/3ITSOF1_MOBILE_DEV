package edu.ap.rentalapp.extensions.instances

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.extensions.RentalService
import edu.ap.rentalapp.extensions.UserService

object RentalServiceSingleton {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    fun getInstance(context: Context): RentalService {
        return RentalService(firestore, context)
    }
}