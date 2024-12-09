package edu.ap.rentalapp.extensions

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.rentalapp.entities.Appliance
import edu.ap.rentalapp.entities.ApplianceDTO
import edu.ap.rentalapp.entities.ApplianceRentalDate
import kotlinx.coroutines.tasks.await
import java.util.Date

class RentalService(private val firestore: FirebaseFirestore = Firebase.firestore, private val context: Context) {
    suspend fun getListOfRentals(): List<Appliance> {
        val appliances = mutableListOf<Appliance>()
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

    suspend fun getListOfRentalsWithDates(): List<ApplianceDTO> {
        val appliances = mutableListOf<ApplianceDTO>()
        try {
            val querySnapshot = firestore.collection("myAppliances").get().await()

            for (document in querySnapshot.documents) {
                val appliance = document.toObject(Appliance::class.java)

                if (appliance != null) {
                    val rentalDates = getRentalDatesForAppliance(appliance.id)

                    val applianceDTO = ApplianceDTO(
                        id = appliance.id,
                        address = appliance.address,
                        category = appliance.category,
                        description = appliance.description,
                        images = appliance.images,
                        latitude = appliance.latitude,
                        longitude = appliance.longitude,
                        name = appliance.name,
                        rentalDates = rentalDates
                    )

                    appliances.add(applianceDTO)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return appliances
    }

    suspend fun getRentalDatesForAppliance(applianceId: String): List<ApplianceRentalDate> {
        val rentalDates = mutableListOf<ApplianceRentalDate>()

        try {
            val rentalQuerySnapshot = firestore.collection("rentalDates")
                .whereEqualTo("applianceId", applianceId)
                .get().await()

            for (rentalDoc in rentalQuerySnapshot.documents) {
                val startDate = rentalDoc.getDate("startDate")
                val endDate = rentalDoc.getDate("endDate")
                val rentedBy =  rentalDoc.getString("rentedByUserId")
                if (startDate != null && endDate != null && rentedBy != null) {
                    val rentalDate = ApplianceRentalDate(
                        applianceId = applianceId,
                        startDate = startDate,
                        endDate = endDate,
                        rentedByUserId = rentedBy
                    )
                    rentalDates.add(rentalDate)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rentalDates
    }

    suspend fun getRentalsByUserId(userId: String): List<ApplianceDTO> {
        return try {
            val rentalDatesSnapshot = firestore.collection("rentalDates")
                .whereEqualTo("rentedByUserId", userId)
                .get().await()

            rentalDatesSnapshot.documents.mapNotNull { rentalDoc ->
                val applianceId = rentalDoc.getString("applianceId") ?: return@mapNotNull null
                val startDate = rentalDoc.getDate("startDate") ?: return@mapNotNull null
                val endDate = rentalDoc.getDate("endDate") ?: return@mapNotNull null

                val applianceSnapshot = firestore.collection("myAppliances").document(applianceId).get().await()
                if (applianceSnapshot.exists()) {
                    val appliance = applianceSnapshot.toObject(ApplianceDTO::class.java) ?: return@mapNotNull null
                    appliance.rentalDates = listOf(
                        ApplianceRentalDate(
                            applianceId = applianceId,
                            startDate = startDate,
                            endDate = endDate,
                            rentedByUserId = userId
                        )
                    )
                    appliance
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
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

    suspend fun getRentalAndDatesById(id: String): ApplianceDTO? {
        return try {
            val applianceSnapshot = firestore.collection("myAppliances").document(id).get().await()
            if (applianceSnapshot.exists()) {
                val appliance = applianceSnapshot.toObject(ApplianceDTO::class.java)
                val rentalDatesSnapshot = firestore.collection("rentalDates")
                    .whereEqualTo("applianceId", id) // Filter rental dates by appliance ID
                    .get().await()
                val rentalDates = rentalDatesSnapshot.documents.mapNotNull { doc ->
                    val startDate = doc.getDate("startDate") ?: return@mapNotNull null
                    val endDate = doc.getDate("endDate") ?: return@mapNotNull null
                    val rentedBy = doc.getString("rentedByUserId") ?: return@mapNotNull null
                    ApplianceRentalDate(
                        applianceId = id,
                        startDate = startDate,
                        endDate = endDate,
                        rentedByUserId = rentedBy
                    )
                }
                appliance?.rentalDates = rentalDates
                appliance
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }
    suspend fun addRentalDateToAppliance(id: String, startDate: Date, endDate: Date, userId: String): Boolean {
        val db = FirebaseFirestore.getInstance()
        val rentalDateRef = db.collection("rentalDates").document()
        val rentalDate = ApplianceRentalDate(
            applianceId = id,
            startDate = startDate,
            endDate = endDate,
            rentedByUserId = userId
        )
        return try {
            rentalDateRef.set(rentalDate).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}