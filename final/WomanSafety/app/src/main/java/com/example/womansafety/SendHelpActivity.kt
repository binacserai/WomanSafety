package com.example.womansafety

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat

class SendHelpActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_help)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize Location Manager and Listener
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Handle location updates if needed
            }

            // Other overridden methods
        }

        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
    }

    // Function to send help message and location to guardians
    fun sendHelp(view: android.view.View) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Retrieve guardian information from Firestore
            val guardianEmails = getGuardianEmailAddressesFromFirestore(userId)
            val guardianPhoneNumbers = getGuardianPhoneNumbersFromFirestore(userId)

            // Send help message via SMS to guardian phone numbers
            for (phoneNumber in guardianPhoneNumbers) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, "Emergency! I need help.", null, null)
            }

            // Send help message via email to guardian email addresses
            val subject = "Emergency Help Request"
            val message = "I am in danger and need help immediately. My location is: <location details>"
            for (email in guardianEmails) {
                sendEmail(email, subject, message)
            }

            Toast.makeText(this, "Help message sent to guardians.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to send an email
    private fun sendEmail(email: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    // Function to retrieve guardian email addresses from Firestore
    private fun getGuardianEmailAddressesFromFirestore(userId: String): List<String> {
        val guardianEmails = mutableListOf<String>()

        val guardiansRef = firestore.collection("users").document(userId).collection("guardians")
        guardiansRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val email = document.getString("email")
                    if (email != null) {
                        guardianEmails.add(email)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure
            }

        return guardianEmails
    }

    // Function to retrieve guardian phone numbers from Firestore
    private fun getGuardianPhoneNumbersFromFirestore(userId: String): List<String> {
        val guardianPhoneNumbers = mutableListOf<String>()

        val guardiansRef = firestore.collection("users").document(userId).collection("guardians")
        guardiansRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val phoneNumber = document.getString("phone")
                    if (phoneNumber != null) {
                        guardianPhoneNumbers.add(phoneNumber)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure
            }

        return guardianPhoneNumbers
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove location updates when activity is destroyed
        locationManager.removeUpdates(locationListener)
    }
}
