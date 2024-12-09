package edu.ap.rentalapp.extensions

import android.content.Context
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
                        rentalDates = rentalDates,
                        userId = appliance.userId
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

                if (startDate != null && endDate != null) {
                    val rentalDate = ApplianceRentalDate(
                        applianceId = applianceId,
                        startDate = startDate,
                        endDate = endDate
                    )
                    rentalDates.add(rentalDate)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rentalDates
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
                    ApplianceRentalDate(
                        applianceId = id,
                        startDate = startDate,
                        endDate = endDate
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
    suspend fun addRentalDateToAppliance(id: String, startDate: Date, endDate: Date): Boolean {
        val db = FirebaseFirestore.getInstance()
        val rentalDateRef = db.collection("rentalDates").document()
        val rentalDate = ApplianceRentalDate(
            applianceId = id,
            startDate = startDate,
            endDate = endDate
        )
        return try {
            rentalDateRef.set(rentalDate).await()
            true
        } catch (e: Exception) {
            false
        }
    }

}