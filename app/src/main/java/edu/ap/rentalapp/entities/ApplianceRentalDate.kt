package edu.ap.rentalapp.entities

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class ApplianceRentalDate(
    @DocumentId val Id: String = "",
    val applianceId: String,
    val startDate: Date,
    val endDate: Date,
    val rentedByUserId: String
) {
    constructor() : this("","", Date(), Date(), "")
}