package com.example.carpoolapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java

class DriverHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_home)

        findViewById<Button>(R.id.btnOfferRide).setOnClickListener {
            startActivity(Intent(this, OfferRideActivity::class.java))
        }

        findViewById<Button>(R.id.btnViewRequests).setOnClickListener {
            startActivity(Intent(this, DriverRequestsActivity::class.java))
        }
        findViewById<Button>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}