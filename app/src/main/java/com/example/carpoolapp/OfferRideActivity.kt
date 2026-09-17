package com.example.carpoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OfferRideActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_ride)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etSource = findViewById<EditText>(R.id.etSource)
        val etDestination = findViewById<EditText>(R.id.etDestination)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etTime = findViewById<EditText>(R.id.etTime)
        val etSeats = findViewById<EditText>(R.id.etSeats)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val btnSubmitRide = findViewById<Button>(R.id.btnSubmitRide)

        btnSubmitRide.setOnClickListener {
            val source = etSource.text.toString().trim()
            val destination = etDestination.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val seatsText = etSeats.text.toString().trim()
            val priceText = etPrice.text.toString().trim()

            if (source.isEmpty() || destination.isEmpty() || date.isEmpty() ||
                time.isEmpty() || seatsText.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val seats = seatsText.toIntOrNull()
            val price = priceText.toIntOrNull()
            if (seats == null || price == null) {
                Toast.makeText(this, "Seats and price must be numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            val ride = Ride(
                driverId = currentUser?.uid ?: "",
                driverEmail = currentUser?.email ?: "",
                source = source,
                destination = destination,
                date = date,
                time = time,
                seats = seats,
                price = price
            )

            db.collection("rides").add(ride)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ride published!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}