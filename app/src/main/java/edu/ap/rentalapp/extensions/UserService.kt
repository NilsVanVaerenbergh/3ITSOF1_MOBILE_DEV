package edu.ap.rentalapp.extensions

import android.content.Context
import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.rentalapp.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserService(private val firestore: FirebaseFirestore = Firebase.firestore, private val context: Context) {

    fun saveUserData(userId: String, username: String, email: String, location: Location?): Flow<Result<Unit>> = callbackFlow {
        val username = email.substringBefore("@")
        val user = mapOf(
            "email" to email,
            "username" to username,
            "location" to location,
            "userId" to userId
        )
        firestore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                trySend(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                trySend(Result.failure(exception))
            }
        awaitClose()
    }

    fun getUserByUserId(userId: String): Flow<Result<Map<String, Any>>> = callbackFlow {
        val db = Firebase.firestore
        val userDocRef = db.collection("users").document(userId)
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    trySend(Result.success(document.data ?: emptyMap()))
                } else {
                    trySend(Result.failure(Exception(context.getString(R.string.error_no_user))))
                }
            }
            .addOnFailureListener { exception ->
                trySend(Result.failure(exception))
            }
        awaitClose()
    }

}