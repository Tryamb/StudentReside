package com.tryamb.studentReside.study.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tryamb.studentReside.R
import com.tryamb.studentReside.databinding.FragmentViewStudyContentBinding


class ViewStudyFragment : Fragment() {
    private  lateinit var binding:FragmentViewStudyContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentViewStudyContentBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        binding.e.setOnClickListener{
            course ="Engineering"
            goToNextFragment(ViewDetailNotes())
        }

        binding.m.setOnClickListener {
            course ="Medical"
            goToNextFragment(ViewDetailNotes())
        }
        binding.s.setOnClickListener {
            course ="SSC"
            goToNextFragment(ViewDetailNotes())
        }
        binding.c.setOnClickListener {
            course ="Commerce"
            goToNextFragment(ViewDetailNotes())
        }
        binding.l.setOnClickListener {
            course ="Law"
            goToNextFragment(ViewDetailNotes())
        }
        binding.o.setOnClickListener {
            course ="Other"
            goToNextFragment(ViewDetailNotes())
        }

        return binding.root
    }

    private fun goToNextFragment(nextFrag:Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()
    }

    companion object{
        var course:String=""
    }

}