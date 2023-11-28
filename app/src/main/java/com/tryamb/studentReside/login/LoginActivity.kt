package com.tryamb.studentReside.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.tryamb.studentReside.R
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    var number : String =""
    // create instance of firebase auth
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_login)
        auth = FirebaseAuth.getInstance()

        // start verification on click of the button
        findViewById<Button>(R.id.btn_get_otp).setOnClickListener {
            login()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                number=findViewById<EditText>(R.id.edt_mobile).text.trim().toString()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
                Log.d("My App", "onVerificationCompleted Success")
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("My App", "onVerificationFailed  $e")
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("My App","onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token

                // Start a new activity using intent
                // also send the storedVerificationId using intent
                // we will use this id to send the otp back to firebase
                val intent = Intent(applicationContext,Verify::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                intent.putExtra("phone", "$number")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun login() {
        number = findViewById<EditText>(R.id.edt_mobile).text.trim().toString()

        // get the phone number from edit text and append the country cde with it
        if (number.isNotEmpty()){
            number = "+91$number"
            sendVerificationCode(number)
        }else{
            Toast.makeText(this,"Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("Student Reside" , "Auth started")
        Toast.makeText(this@LoginActivity,"Auth Started, please wait few seconds",Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

    }
}