package com.tryamb.studentReside.vendor.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.adapter.VendorTiffinRecycler
import com.tryamb.studentReside.databinding.FragmentListTiffinBinding
import com.tryamb.studentReside.model.Tiffin
import com.tryamb.studentReside.util.ConnectionManager


class ListTiffin : Fragment() {
    private lateinit var binding: FragmentListTiffinBinding
    private lateinit var recyclerAdapter: VendorTiffinRecycler
    private lateinit var tiffinList: ArrayList<Tiffin>
    private val regMob= Firebase.auth.currentUser?.phoneNumber
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentListTiffinBinding.inflate(layoutInflater,container,false)
       tiffinList= arrayListOf()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.setHasFixedSize(true)
        recyclerAdapter = VendorTiffinRecycler(requireContext(), tiffinList)
        binding.recycler.adapter = recyclerAdapter
        return binding.root
    }

    private fun showData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("MealCenter")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(
                            requireContext(),
                            "Some unexpected error occurred!!! ",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    for (tiffinData: DocumentChange in value?.documentChanges!!) {
                        if (tiffinData.type == DocumentChange.Type.ADDED) {
                            val mobileNo = tiffinData.document.getString("registeredContact")
                            if(mobileNo!=null && mobileNo==regMob) {
                                tiffinList.add(tiffinData.document.toObject(Tiffin::class.java))
                            }
                        }
                    }
                    recyclerAdapter.notifyDataSetChanged()
                    //binding.progressLayout.visibility = View.GONE
                }
            })
    }
    override fun onResume() {
        if (ConnectionManager().checkConnectivity(requireContext())) {
            if (tiffinList.isEmpty())
                showData()
        } else {
            checkInternet()
        }
        super.onResume()
    }
    private fun checkInternet() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(requireActivity())
        }
        dialog.create()
        dialog.show()
    }

}