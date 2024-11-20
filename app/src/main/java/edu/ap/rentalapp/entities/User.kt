package edu.ap.rentalapp.entities

import android.os.Parcel
import android.os.Parcelable

data class User(
    var userId: String = "",
    var username: String = "",
    var email: String = "",
    var lat: String = "",
    var lon: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        userId = parcel.readString() ?: "",
        username = parcel.readString() ?: "",
        email = parcel.readString() ?: "",
        lat = parcel.readString() ?: "",
        lon = parcel.readString() ?: "",
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(lat)
        parcel.writeString(lon)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel): User {
                return User(parcel)
            }
            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }
    }
}