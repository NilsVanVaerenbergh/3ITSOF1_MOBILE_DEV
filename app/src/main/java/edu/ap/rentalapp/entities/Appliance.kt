package edu.ap.rentalapp.entities

data class Appliance(
    var address: String = "",
    var category: String = "",
    var description: String = "",
    var images: List<String> = emptyList(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var name: String = ""
) {
    constructor() : this("", "", "", emptyList(), 0.0, 0.0, "")
}