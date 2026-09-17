package com.example.carpoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RideDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_details)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val rideId = intent.getStringExtra("rideId") ?: ""
        val source = intent.getStringExtra("source") ?: ""
        val destination = intent.getStringExtra("destination") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val price = intent.getIntExtra("price", 0)
        val driverId = intent.getStringExtra("driverId") ?: ""
        val driverEmail = intent.getStringExtra("driverEmail") ?: ""

        findViewById<TextView>(R.id.tvDetailRoute).text = "$source → $destination"
        findViewById<TextView>(R.id.tvDetailDateTime).text = "$date, $time"
        findViewById<TextView>(R.id.tvDetailPrice).text = "₹$price per seat"
        findViewById<TextView>(R.id.tvDetailDriver).text = "Driver: $driverEmail"

        findViewById<Button>(R.id.btnBookRide).setOnClickListener {
            val currentUser = auth.currentUser
            val booking = Booking(
                rideId = rideId,
                driverId = driverId,
                passengerId = currentUser?.uid ?: "",
                passengerEmail = currentUser?.email ?: "",
                source = source,
                destination = destination,
                date = date,
                time = time,
                price = price,
                status = "pending"
            )

            db.collection("bookings").add(booking)
                .addOnSuccessListener {
                    Toast.makeText(this, "Booking request sent!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}