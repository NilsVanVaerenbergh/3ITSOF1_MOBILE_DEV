package edu.ap.rentalapp.extensions

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.rentalapp.entities.Appliance
import kotlinx.coroutines.tasks.await

class RentalService(private val firestore: FirebaseFirestore = Firebase.firestore, private val context: Context) {
    suspend fun getListOfRentals(): List<Appliance> {
        val appliances = mutableListOf<Appliance>() // Mutable list to store results
        try {
            val querySnapshot = firestore.collection("myAppliances").get().await()
            for (document in querySnapshot.documents) {
                val appliance = document.toObject(Appliance::class.java)
                if (appliance != null) {
                    appliances.add(appliance)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return appliances
    }
    suspend fun getRentalById(id: String): Appliance? {
        return try {
            val documentSnapshot = firestore.collection("myAppliances").document(id).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Appliance::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}