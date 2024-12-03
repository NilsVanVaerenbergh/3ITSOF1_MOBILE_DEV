package edu.ap.rentalapp.entities

import java.util.Date

data class ApplianceRentalDate(
    val applianceId: String,
    val startDate: Date,
    val endDate: Date
) {
    constructor() : this("", Date(), Date())
}