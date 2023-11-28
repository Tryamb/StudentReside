package com.tryamb.studentReside.vendor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tryamb.studentReside.R
import com.tryamb.studentReside.databinding.ActivityVendorDashboardBinding
import com.tryamb.studentReside.vendor.fragments.Dashboard

class VendorDashboard : AppCompatActivity() {
lateinit var binding: ActivityVendorDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityVendorDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadFragment(Dashboard())

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frag, fragment)
            .commit()
    }
}