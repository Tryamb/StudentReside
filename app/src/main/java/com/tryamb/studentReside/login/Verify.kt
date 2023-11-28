package com.tryamb.studentReside.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.*
import com.mukesh.OtpView
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.student.HomeActivity
import com.tryamb.studentReside.vendor.VendorDashboard
import com.tryamb.studentReside.databinding.ActivityVerifyBinding

class Verify : AppCompatActivity() {
    lateinit var binding: ActivityVerifyBinding
    lateinit var auth: FirebaseAuth
    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // get storedVerificationId from the intent
        val storedVerificationId = intent.getStringExtra("storedVerificationId")
        //get mobile number
        val getMobNo = intent.getStringExtra("phone")
        binding.txtCodeissentto.text = getString(R.string.msg_code_is_sent_to) + " " + getMobNo
        // fill otp and call the on click on button
        findViewById<Button>(R.id.btnVerifyAndContinue).setOnClickListener {
            val otp = findViewById<OtpView>(R.id.otpViewOtpview).text!!.trim().toString()
            if (otp.isNotEmpty()) {
                dialog = ProgressDialog(this@Verify)
                dialog!!.setMessage("Verifying...")
                dialog!!.show()
                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp
                )
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    dialog!!.dismiss()
                    val user = FirebaseAuth.getInstance().currentUser
                    val phoneNumber = user!!.phoneNumber.toString()
                    profileAlreadyRegistered(phoneNumber) { result ->
                        if (result == "vendor") {
                            val intent = Intent(this, VendorDashboard::class.java)
                            startActivity(intent)
                            finish()
                        } else if (result == "student") {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, ProfileSelection::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun profileAlreadyRegistered(newMobNo: String, callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred!!! ",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    for (userData: DocumentChange in value?.documentChanges!!) {
                        val mobileNo = userData.document.getString("mobile")
                        if (mobileNo != null && mobileNo == newMobNo) {
                            val profile = userData.document.getString("type")!!
                            callback(profile)
                            return
                        }
                    }

                    // If no matching document found
                    callback("") // Return an empty string as the profile is not registered
                }
            })
    }


}