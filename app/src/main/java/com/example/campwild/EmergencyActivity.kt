package com.example.campwild

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class EmergencyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back button

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.title = "Camp Wild"
        val rescueNumberTextView = findViewById<TextView>(R.id.rescue_number_text)
        val emergencyNumberTextView = findViewById<TextView>(R.id.emergency_number_text)
        rescueNumberTextView.text = "Contact Scottish Mountain Rescue on " + MOUNTAIN_RESCUE_NUMBER
        emergencyNumberTextView.text = "Or in an emergency, dial " + EMERGENCY_NUMBER
        rescueNumberTextView.setOnClickListener { callPhoneNumber(MOUNTAIN_RESCUE_NUMBER) }
        emergencyNumberTextView.setOnClickListener { callPhoneNumber(EMERGENCY_NUMBER) }

    }

    private fun callPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    companion object {
        private const val MOUNTAIN_RESCUE_NUMBER = "01479 861370"
        private const val EMERGENCY_NUMBER = "999"
    }
}
