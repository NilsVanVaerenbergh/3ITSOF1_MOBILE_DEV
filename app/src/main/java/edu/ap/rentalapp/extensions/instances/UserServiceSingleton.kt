package edu.ap.rentalapp.extensions.instances

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.extensions.UserService

object UserServiceSingleton {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    fun getInstance(context: Context): UserService {
        return UserService(firestore, context)
    }
    val Context.dataStore by preferencesDataStore(name = "user_preferences")

}