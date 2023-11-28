package com.tryamb.studentReside.messages.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.student.HomeFragment
import com.tryamb.studentReside.adapter.NotificationAdapter
import com.tryamb.studentReside.model.Notification

class HostelNotificationAndOpted : Fragment() {
    val studentContact= Firebase.auth.currentUser?.phoneNumber
    var mAdapter: NotificationAdapter? = null
    var recyclerView: RecyclerView? = null
    private lateinit var hList: ArrayList<Notification>
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView=inflater.inflate(R.layout.fragment_hostel_notification_and_opted, container, false)
        hList = arrayListOf()
        recyclerView = rootView.findViewById(R.id.rv)
        if(isAdded) {
            recyclerView?.layoutManager = LinearLayoutManager(requireContext())
            recyclerView?.setHasFixedSize(true)
            mAdapter = NotificationAdapter(requireContext(), hList)
            recyclerView!!.adapter = mAdapter
        }

        setupBackPressedCallback()

        return rootView
    }
    private fun readNotification() {
        hList.clear()
        val dbref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Notification")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val ds = dataSnapshot1.getValue(Notification::class.java)!!
                    val currStdContact=ds.phone
                    if(studentContact==currStdContact){
                        hList.add(ds)

                    }
                    mAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onResume() {
        super.onResume()
        readNotification()
    }
    private fun setupBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateFragment(HomeFragment())
            }
        })
    }

    private fun navigateFragment(nextFrag:Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainHolder, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()
    }


}