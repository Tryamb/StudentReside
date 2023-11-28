package com.tryamb.studentReside.study.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tryamb.studentReside.R
import com.tryamb.studentReside.login.ProfileSelection
import com.tryamb.studentReside.model.StudyMaterial


 class UploadStudyFragment : Fragment() {
    private var db: FirebaseFirestore? = null
    private var upload: Button? = null
    var imageuri: Uri? = null
    var topicName: String= ""
    var courseName: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val v = inflater.inflate(R.layout.fragment_upload_study_content, container, false)
        db = FirebaseFirestore.getInstance()
        upload = v.findViewById(R.id.btn_upload)
        upload!!.setOnClickListener {
            topicName = v.findViewById<EditText>(R.id.etTopic).text.toString().trim()
            if (topicName.isEmpty()){
                v.findViewById<EditText>(R.id.etTopic).error = "Please enter topic name"
            } else if (courseName.isEmpty()) {
                v.findViewById<EditText>(R.id.selectCourse).error = "Please choose course type"
            } else {
                
            v.findViewById<EditText>(R.id.etTopic).text.clear()
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT

            // will be redirected to choose pdf
            galleryIntent.type = "application/pdf"
            startActivityForResult(galleryIntent, 1) }
        }
        val radioGroup = v.findViewById<View>(R.id.radio_group) as RadioGroup

        radioGroup.setOnCheckedChangeListener { _, checkedId -> // checkedId is the RadioButton selected
            when (checkedId) {
                R.id.e ->  {
                    courseName= "Engineering"
                }
                R.id.m -> {
                    courseName = "Medical"
                }

                R.id.s ->  {
                    courseName = "SSC"
                }

                R.id.c ->  {
                    courseName = "Commerce"
                }
                R.id.l ->  {
                    courseName = "Law"
                }
                R.id.o ->  {
                    courseName = "Other"
                }
            }
        }
        return v
    }

    private var dialog: ProgressDialog? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            dialog = ProgressDialog(requireContext())
            dialog!!.setMessage("Uploading")


            dialog!!.show()
            imageuri = data!!.data

            val timestamp = "" + System.currentTimeMillis()

            val ref: StorageReference =
                FirebaseStorage.getInstance().reference.child("$timestamp.pdf")
            imageuri?.let {
                ref.putFile(it)
                    .addOnSuccessListener { taskSnapshot ->
                        dialog!!.dismiss()
                        val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val downloadUri =
                            uriTask.result.toString() // getting url if task is successful
                        if (uriTask.isSuccessful) {
                            addDataToFirestore(courseName,topicName,downloadUri)

                            Toast.makeText(requireContext(), "Uploaded Successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }.addOnFailureListener(OnFailureListener { })
            }
        }

    }
    private fun addDataToFirestore(course: String, topic: String, downloadUri: String) {
        val dbStudyMaterial = db!!.collection("StudyMaterial")

        val contentPdfInfo = StudyMaterial(course,topic,downloadUri)

        dbStudyMaterial.add(contentPdfInfo).addOnSuccessListener {

        }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update \n$e", Toast.LENGTH_SHORT)
                    .show()
            }
    }




}