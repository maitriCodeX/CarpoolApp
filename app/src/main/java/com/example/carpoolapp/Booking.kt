package com.example.carpoolapp

data class Booking(
    var bookingId: String = "",
    var rideId: String = "",
    var driverId: String = "",
    var passengerId: String = "",
    var passengerEmail: String = "",
    var source: String = "",
    var destination: String = "",
    var date: String = "",
    var time: String = "",
    var price: Int = 0,
    var status: String = "pending"
)