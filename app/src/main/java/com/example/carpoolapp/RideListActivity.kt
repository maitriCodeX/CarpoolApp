package com.example.carpoolapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RideListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_list)

        db = FirebaseFirestore.getInstance()
        val rvRides = findViewById<RecyclerView>(R.id.rvRides)
        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
        rvRides.layoutManager = LinearLayoutManager(this)

        bottomNavigation.selectedItemId = R.id.nav_view_rides

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, RoleSelectionActivity::class.java))
                    true
                }
                R.id.nav_view_rides -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, MyBookingsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, EditProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        db.collection("rides").get()
            .addOnSuccessListener { result ->
                val rideList = mutableListOf<Ride>()
                for (doc in result) {
                    val ride = doc.toObject(Ride::class.java)
                    ride.rideId = doc.id
                    rideList.add(ride)
                }

                val adapter = RideAdapter(rideList) { selectedRide ->
                    val intent = Intent(this, RideDetailsActivity::class.java)
                    intent.putExtra("rideId", selectedRide.rideId)
                    intent.putExtra("source", selectedRide.source)
                    intent.putExtra("destination", selectedRide.destination)
                    intent.putExtra("date", selectedRide.date)
                    intent.putExtra("time", selectedRide.time)
                    intent.putExtra("price", selectedRide.price)
                    intent.putExtra("driverId", selectedRide.driverId)
                    intent.putExtra("driverEmail", selectedRide.driverEmail)
                    startActivity(intent)
                }
                rvRides.adapter = adapter
            }
    }
}