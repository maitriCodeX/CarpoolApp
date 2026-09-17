package com.example.carpoolapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DriverRequestsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var rvRequests: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_requests)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        rvRequests = findViewById(R.id.rvRequests)
        rvRequests.layoutManager = LinearLayoutManager(this)

        loadBookings()
    }

    private fun loadBookings() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("bookings").whereEqualTo("driverId", uid).get()
            .addOnSuccessListener { result ->
                val bookingList = mutableListOf<Booking>()
                for (doc in result) {
                    val booking = doc.toObject(Booking::class.java)
                    booking.bookingId = doc.id
                    bookingList.add(booking)
                }

                val adapter = BookingAdapter(
                    bookingList,
                    onAccept = { booking -> updateStatus(booking, "accepted") },
                    onReject = { booking -> updateStatus(booking, "rejected") }
                )
                rvRequests.adapter = adapter
            }
    }

    private fun updateStatus(booking: Booking, newStatus: String) {
        db.collection("bookings").document(booking.bookingId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking $newStatus", Toast.LENGTH_SHORT).show()
                loadBookings() // refresh the list so the UI reflects the new status
            }
    }
}