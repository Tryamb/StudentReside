package com.tryamb.studentReside.vendor.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.tryamb.studentReside.databinding.FragmentAddTiffinCentersBinding
import com.tryamb.studentReside.model.Hostel
import com.tryamb.studentReside.model.Tiffin


class AddTiffinCenters : Fragment() {

lateinit var binding:FragmentAddTiffinCentersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddTiffinCentersBinding.inflate(layoutInflater,container,false)
        binding.btnAdd.setOnClickListener{
            var name=binding.tiffinCenterName.text.toString().trim()
            var address=binding.tiffinAddress.text.toString().trim()
            var fee=binding.tiffinFee.text.toString().trim()
            var contact=binding.tiffinContactNo.text.toString().trim()

            binding.tiffinCenterName.text.clear()
            binding.tiffinAddress.text.clear()
            binding.tiffinFee.text.clear()
            binding.tiffinContactNo.text.clear()
            
            if(name.isEmpty() || address.isEmpty() || fee.isEmpty()  || contact.isEmpty())
            {
                Toast.makeText(requireContext(),"Fill All the fields", Toast.LENGTH_SHORT).show()
            }
            else {
                // calling method to add data to Firebase Firestore.
                addDataToFirestore(name,address,
                    contact,fee)
            }
        }

        return binding.root
    }

    private fun addDataToFirestore(
        name: String,
        address:String,
        contact: String,
        fee: String
    ) {
        val db= FirebaseFirestore.getInstance()
        val dbTiffin = db!!.collection("MealCenter")

        val tiffin = Tiffin(address,fee,
            contact,name,
            Hostel().Rating)

        dbTiffin.add(tiffin).addOnSuccessListener {
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


}