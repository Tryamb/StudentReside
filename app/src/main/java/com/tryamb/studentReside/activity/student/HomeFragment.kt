package com.tryamb.studentReside.activity.student

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.appUtils.Pref
import com.tryamb.studentReside.databinding.FragmentHomeBinding
import com.tryamb.studentReside.login.LoginActivity
import com.tryamb.studentReside.study.StudyZone


class HomeFragment : Fragment() {
    lateinit var binding:FragmentHomeBinding
    var backPressedTime:Long=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentHomeBinding.inflate(layoutInflater,container,false)
        binding.goToHostels.setOnClickListener {
            val intent = Intent(requireContext(), HostelActivity::class.java)
            startActivity(intent)
        }

        binding.goToTiffinCenters.setOnClickListener {
            val intent = Intent(requireContext(), TiffinActivity::class.java)
            startActivity(intent)
        }

        binding.goToSafetyService.setOnClickListener {
            val intent = Intent(requireContext(), StudyZone::class.java)
            startActivity(intent)
        }

        binding.goToHostelMateZone.setOnClickListener {
            val intent = Intent(requireContext(), HostelMateZoneActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignOut.setOnClickListener {
            Firebase.auth.signOut()
            Pref(requireContext()).clearAll()
            Toast.makeText(requireContext(), "Signed Out Successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        setupBackPressedCallback()

        return binding.root
    }

    private fun setupBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(backPressedTime+3000>System.currentTimeMillis()) {
                    activity?.finishAffinity()
                }
                else{
                    Toast.makeText(requireContext(),"Press back again to exit",Toast.LENGTH_SHORT).show()
                }
                backPressedTime=System.currentTimeMillis()
            }
        })
    }




}