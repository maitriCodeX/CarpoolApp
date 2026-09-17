package com.example.carpoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class DriverEditRideActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var rideId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_edit_ride)

        db = FirebaseFirestore.getInstance()

        rideId = intent.getStringExtra("rideId") ?: ""

        val etSource = findViewById<EditText>(R.id.etEditSource)
        val etDestination = findViewById<EditText>(R.id.etEditDestination)
        val etDate = findViewById<EditText>(R.id.etEditDate)
        val etTime = findViewById<EditText>(R.id.etEditTime)
        val etSeats = findViewById<EditText>(R.id.etEditSeats)
        val etPrice = findViewById<EditText>(R.id.etEditPrice)

        etSource.setText(intent.getStringExtra("source") ?: "")
        etDestination.setText(intent.getStringExtra("destination") ?: "")
        etDate.setText(intent.getStringExtra("date") ?: "")
        etTime.setText(intent.getStringExtra("time") ?: "")
        etSeats.setText(intent.getIntExtra("seats", 0).toString())
        etPrice.setText(intent.getIntExtra("price", 0).toString())

        findViewById<Button>(R.id.btnSaveRide).setOnClickListener {
            val updateMap = mapOf(
                "source" to etSource.text.toString().trim(),
                "destination" to etDestination.text.toString().trim(),
                "date" to etDate.text.toString().trim(),
                "time" to etTime.text.toString().trim(),
                "seats" to etSeats.text.toString().trim().toIntOrNull(),
                "price" to etPrice.text.toString().trim().toIntOrNull()
            )

            db.collection("rides").document(rideId).update(updateMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ride updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

        findViewById<Button>(R.id.btnDeleteRideAction).setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Delete Ride")
                .setMessage("Are you sure you want to delete this ride?")
                .setPositiveButton("Delete") { _, _ ->
                    db.collection("rides").document(rideId).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Ride deleted successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}