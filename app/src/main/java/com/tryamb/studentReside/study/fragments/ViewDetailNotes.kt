package com.tryamb.studentReside.study.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.tryamb.studentReside.adapter.StudyRecyclerAdapter
import com.tryamb.studentReside.databinding.FragmentViewDetailNotesBinding
import com.tryamb.studentReside.model.StudyMaterial
import com.tryamb.studentReside.util.ConnectionManager

class ViewDetailNotes : Fragment() {
    var currCourse=""
    private lateinit var binding:FragmentViewDetailNotesBinding
    private lateinit var recyclerAdapter: StudyRecyclerAdapter
    private lateinit var studyList: ArrayList<StudyMaterial>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentViewDetailNotesBinding.inflate(layoutInflater,container,false)
        studyList = arrayListOf()

        currCourse= ViewStudyFragment.course


        binding.recyclerNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotes.setHasFixedSize(true)
        recyclerAdapter = StudyRecyclerAdapter(requireContext(), studyList)
        binding.recyclerNotes.adapter = recyclerAdapter
        return binding.root
    }
    override fun onResume() {
        if (ConnectionManager().checkConnectivity(requireContext())) {
            if (studyList.isEmpty())
                showData(currCourse)
        } else {
            checkInternet()
        }
        super.onResume()
    }

    private fun showData(currCourse:String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("StudyMaterial")
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
                    for (studyMaterial: DocumentChange in value?.documentChanges!!) {
                        val course = studyMaterial.document.getString("course")
                        if (course != null && course == currCourse) {
                            studyList.add(studyMaterial.document.toObject(StudyMaterial::class.java))

                        }
                    }
                    recyclerAdapter.notifyDataSetChanged()
                }
            })
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