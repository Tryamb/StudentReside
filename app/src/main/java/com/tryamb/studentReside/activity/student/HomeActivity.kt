package com.tryamb.studentReside.activity.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.student.HomeFragment
import com.tryamb.studentReside.appUtils.Pref
import com.tryamb.studentReside.databinding.ActivityHomeBinding
import com.tryamb.studentReside.messages.fragments.ChatListFragment
import com.tryamb.studentReside.messages.fragments.HostelNotificationAndOpted

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val userMobNo=Firebase.auth.currentUser?.phoneNumber

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentName(userMobNo!!){
            if(it.isNotEmpty()){
                Pref(this).putString("studentName",it)
                loadFragment(HomeFragment())
            }
        }

        binding.notification.setOnClickListener {
            loadFragment(HostelNotificationAndOpted())
        }

        binding.messenger.setOnClickListener {
            loadFragment(ChatListFragment())
        }

    }
    private fun studentName(newMobNo: String, userName: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    for (userData: DocumentChange in value?.documentChanges!!) {
                        val mobileNo = userData.document.getString("mobile")
                        val profile = userData.document.getString("type")!!
                        if (mobileNo != null && mobileNo.takeLast(10) == newMobNo.takeLast(10) && profile=="student") {
                            val name = userData.document.getString("name")!!
                            userName(name)
                            return
                        }
                    }

                }
            })
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainHolder, fragment)
            .commit()
    }
}