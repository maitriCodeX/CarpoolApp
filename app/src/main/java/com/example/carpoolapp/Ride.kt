package com.example.carpoolapp

data class Ride(
    var rideId: String = "",
    var driverId: String = "",
    var driverEmail: String = "",
    var source: String = "",
    var destination: String = "",
    var date: String = "",
    var time: String = "",
    var seats: Int = 0,
    var price: Int = 0
)