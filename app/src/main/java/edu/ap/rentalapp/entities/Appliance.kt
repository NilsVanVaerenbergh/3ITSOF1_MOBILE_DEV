package edu.ap.rentalapp.entities

import com.google.firebase.firestore.DocumentId

data class Appliance(
    @DocumentId var id: String = "",
    var address: String = "",
    var category: String = "",
    var description: String = "",
    var images: List<String> = emptyList(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var name: String = "",
    var pricePerDay: Int = 0,
    var userId: String = ""
) {
    constructor() : this("", "", "", "",emptyList(), 0.0, 0.0, "", 0,"")
}