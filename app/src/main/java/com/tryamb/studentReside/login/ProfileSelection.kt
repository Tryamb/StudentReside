package com.tryamb.studentReside.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.student.HomeActivity
import com.tryamb.studentReside.vendor.VendorDashboard
import com.tryamb.studentReside.databinding.ActivityProfileSelectionBinding
import com.tryamb.studentReside.model.UserDetails


class ProfileSelection : AppCompatActivity() {
    lateinit var binding:ActivityProfileSelectionBinding

    private var db: FirebaseFirestore? = null
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileSelectionBinding.inflate(layoutInflater)

        setContentView(binding.root)

        db = FirebaseFirestore.getInstance();

        val submitCourseBtn = binding.btnGetStarted

        submitCourseBtn.setOnClickListener{
            val name = binding.etName.text.toString().trim()
            val user = FirebaseAuth.getInstance().currentUser
            val phoneNumber = user!!.phoneNumber.toString()
                if (name.isEmpty()){
                    binding.etName.error = "Please enter your Name"
                } else if (profileType.isEmpty()) {
                    binding.selectProfile.error = "Please choose one of the account type"
                } else {
                    // calling method to add data to Firebase Firestore.
                    addDataToFirestore(name, phoneNumber, profileType)
                }
            }

    }

    private fun addDataToFirestore(name: String, phoneNumber: String, profileType: String) {
        val dbUsers = db!!.collection("Users")

        val user = UserDetails(phoneNumber,name,profileType)

        dbUsers.add(user).addOnSuccessListener {
            Toast.makeText(
                this@ProfileSelection,
                "Profile Created",
                Toast.LENGTH_SHORT
            ).show()
            if(profileType=="vendor"){
                startActivity(Intent(this@ProfileSelection, VendorDashboard::class.java))
                finish()
            }
            else if(profileType=="student"){
                startActivity(Intent(this@ProfileSelection, HomeActivity::class.java))
                finish()
            }
        }
            .addOnFailureListener { e ->
                Toast.makeText(this@ProfileSelection, "Fail to create profile \n$e", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun radio_button_click(view: View) {
        if (view is RadioButton) {
            val checked = (view as RadioButton).isChecked
            when (view.getId()) {
                R.id.v -> if (checked) {
                    profileType = "vendor"
                }
                R.id.s -> if (checked) {
                    profileType = "student"
                }
            }
        }
    }


    companion object{
        var profileType=""
    }


}