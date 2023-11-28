package com.tryamb.studentReside.activity.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.databinding.ActivityHostelMateZoneBinding

class HostelMateZoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostelMateZoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostelMateZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFeedback.setOnClickListener {
            sendFeedback()
        }
    }

    private fun sendFeedback() {

    }

}