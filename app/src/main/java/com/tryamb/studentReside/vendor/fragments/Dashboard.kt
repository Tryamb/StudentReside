package com.tryamb.studentReside.vendor.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.databinding.FragmentDashboardBinding
import com.tryamb.studentReside.login.LoginActivity

class Dashboard : Fragment() {

lateinit var binding: FragmentDashboardBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentDashboardBinding.inflate(layoutInflater,container,false)

        binding.addHostels.setOnClickListener {
            goToNextFragment(AddHostels())
        }
        binding.addTiffinCenters.setOnClickListener {
            goToNextFragment(AddTiffinCenters())
        }
        binding.manageHostels.setOnClickListener {
            goToNextFragment(ListHostel())
        }
        binding.manageTiffinCenters.setOnClickListener {
            goToNextFragment(ListTiffin())
        }

        binding.btnSignOut.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(requireContext(), "Signed Out Successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun goToNextFragment(nextFrag:Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frag, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()
    }


}