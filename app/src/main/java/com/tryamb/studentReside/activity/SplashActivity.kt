package com.tryamb.studentReside.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.student.HomeActivity
import com.tryamb.studentReside.vendor.VendorDashboard
import com.tryamb.studentReside.login.LoginActivity
import com.tryamb.studentReside.login.ProfileSelection
import com.tryamb.studentReside.login.Verify

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //splash screen handler
        Handler().postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if(user!=null){
                val phoneNumber = user!!.phoneNumber.toString()
                Verify().profileAlreadyRegistered(phoneNumber) { result ->
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
            }
            else {
                val startAct = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(startAct)
            }
        }, 2000)

    }
}