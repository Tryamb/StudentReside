package com.tryamb.studentReside.vendor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.databinding.FragmentAddHostelsBinding
import com.tryamb.studentReside.model.Hostel

class AddHostels : Fragment() {
    private lateinit var detailList: ArrayList<String>
    private lateinit var binding:FragmentAddHostelsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddHostelsBinding.inflate(layoutInflater,container,false)
        detailList= arrayListOf()

        binding.type.setOnCheckedChangeListener { group, checkedId -> // checkedId is the RadioButton selected
            when (checkedId) {
                R.id.b ->  {
                    hostel_type = "Boys"
                }
                R.id.g -> {
                    hostel_type = "Girls"
                }

            }
        }


        binding.btnAdd.setOnClickListener{
            var hostel_Name=binding.hostelName.text.toString().trim()
            var hostel_Address=binding.hostelAddress.text.toString().trim()
            var hostel_Fee=binding.hostelFee.text.toString().trim()
            var hostel_Timing=binding.hostelTiming.text.toString().trim()
            var hostel_Contact=binding.hostelContactNo.text.toString().trim()
            var other_Detail=binding.otherDetail.text.toString().trim()
            binding.hostelName.text.clear()
            binding.hostelAddress.text.clear()
            binding.hostelTiming.text.clear()
            binding.hostelContactNo.text.clear()
            binding.otherDetail.text.clear()
            detailList.clear()

            if(binding.c1.isChecked){
                detailList.add(binding.c1.text.toString())
            }

            if(binding.c2.isChecked){
                detailList.add(binding.c2.text.toString())
            }

            if(binding.c3.isChecked){
                detailList.add(binding.c3.text.toString())
            }

            if(binding.c4.isChecked){
                detailList.add(binding.c4.text.toString())
            }

            if(binding.c5.isChecked){
                detailList.add(binding.c5.text.toString())
            }

            if(binding.c6.isChecked){
                detailList.add(binding.c6.text.toString())
            }

            if(binding.c7.isChecked){
                detailList.add(binding.c7.text.toString())
            }
            if(other_Detail.isNotEmpty()){
                detailList.add(other_Detail)
            }

            if(hostel_Name.isEmpty() || hostel_Address.isEmpty() || hostel_Fee.isEmpty() || hostel_Timing.isEmpty() || hostel_Contact.isEmpty() || detailList.isEmpty())
            {
                Toast.makeText(requireContext(),"Fill All the fields", Toast.LENGTH_SHORT).show()
            }
            else {
                // calling method to add data to Firebase Firestore.
                addDataToFirestore(hostel_Name,  hostel_type, hostel_Address, hostel_Contact, hostel_Fee, detailList, hostel_Timing)
            }
        }

        return binding.root
    }
    private fun addDataToFirestore(name: String, hostelType: String, hostelAddress: String, hostelContact:String, hostelFee:String, hostelDetails:ArrayList<String>,inTime:String) {
        val registeredMob= Firebase.auth.currentUser?.phoneNumber
        val hostelId="h"+System.currentTimeMillis().toString()

        val db=FirebaseFirestore.getInstance()
        val hostels = db!!.collection("Hostels")

        val hostel = Hostel(hostelAddress,hostelFee,hostelContact,name,Hostel().Rating,
            inTime,hostelType,hostelDetails,registeredMob,
            hostelId,Hostel().image1,Hostel().image2,Hostel().image3,Hostel().video)

        hostels.add(hostel).addOnSuccessListener {
            Toast.makeText(
                requireContext(),
                "Details added successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Fail to create profile \n$e", Toast.LENGTH_SHORT)
                    .show()
            }
    }
companion object{
    var hostel_type:String=""
}

}