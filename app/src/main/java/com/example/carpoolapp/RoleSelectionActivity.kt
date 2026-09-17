package com.example.carpoolapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RoleSelectionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnDriver = findViewById<Button>(R.id.btnDriver)
        val btnPassenger = findViewById<Button>(R.id.btnPassenger)

        btnDriver.setOnClickListener {
            saveRole("driver")
            startActivity(Intent(this, DriverHomeActivity::class.java))
        }

        btnPassenger.setOnClickListener {
            saveRole("passenger")
            startActivity(Intent(this, PassengerHomeActivity::class.java))
        }
    }

    private fun saveRole(role: String) {
        val uid = auth.currentUser?.uid ?: return
        val userMap = hashMapOf("uid" to uid, "email" to auth.currentUser?.email, "role" to role)
        db.collection("users").document(uid).set(userMap)
    }
}