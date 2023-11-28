package com.tryamb.studentReside.vendor

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.ImageActivity
import com.tryamb.studentReside.adapter.ListedPersonAdapter
import com.tryamb.studentReside.adapter.NotificationAdapter
import com.tryamb.studentReside.databinding.ActivityManageHostelBinding
import com.tryamb.studentReside.model.Notification
import kotlinx.android.synthetic.main.activity_manage_hostel.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


class ManageHostelActivity : AppCompatActivity() {

    var number : String =""
    private var img1:String?=null
    private var img2:String?=null
    private var img3:String?=null
    private var progressDialog: ProgressDialog? = null
    private var db: FirebaseFirestore? = null
    private var id:String?=null
    private var hostelName:String?=null
    private var hostelContact:String?=null
    private var imageuri: Uri? = null
    private var videouri: Uri? = null
    var mAdapter: ListedPersonAdapter? = null
    var recyclerView: RecyclerView? = null
    private lateinit var studentList: ArrayList<Notification>
    lateinit var binding:ActivityManageHostelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityManageHostelBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        studentList = arrayListOf()
        recyclerView = findViewById(R.id.rvStd)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        mAdapter = ListedPersonAdapter(this@ManageHostelActivity, studentList,"vendor")
        recyclerView!!.adapter = mAdapter

        //loading images and other details

        db = FirebaseFirestore.getInstance()
        id=intent.getStringExtra("id")
        hostelName=intent.getStringExtra("hostelName")
        hostelContact=intent.getStringExtra("hostelContact")

        binding.img1.setOnClickListener{
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT

            // will be redirected to choose pdf
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 1)
        }
        binding.img2.setOnClickListener{
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        }
        binding.img3.setOnClickListener{
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT

            // will be redirected to choose pdf
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 3)
        }
        binding.video.setOnClickListener {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 4)
        }

        binding.dbImg1.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra("img", img1)
            startActivity(intent)
        }
        binding.dbImg2.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra("img", img2)
            startActivity(intent)
        }
        binding.dbImg3.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra("img", img3)
            startActivity(intent)
        }

        binding.btnGetOtp.setOnClickListener {
            val phone = "+91"+binding.edtMobile.text.toString().trim()
            if (phone.isNotEmpty()) {
                studentProfileExist(phone){ response ->  //response is person name
                    if(response.isEmpty()){
                        Toast.makeText(this,"Person is not registered in this app with student account",Toast.LENGTH_LONG).show()

                    }
                    else{
                        addRealTimeDB(id!!,phone,response,hostelName!!,hostelContact!!)
                    }
                }

            }
        }

    }

    private fun addRealTimeDB(id: String, phone: String, name:String, hostelName:String,hostelContact:String) {
        val ref = FirebaseDatabase.getInstance().getReference("Notification")
            .child(id+phone)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!dataSnapshot.exists()){
                    ref.child("h_id").setValue(id)
                    ref.child("phone").setValue(phone)
                    ref.child("h_name").setValue(hostelName)
                    ref.child("h_phone").setValue(hostelContact)
                    ref.child("name").setValue(name)
                    ref.child("status").setValue("")
                    Toast.makeText(this@ManageHostelActivity,"Person is notified successfully",Toast.LENGTH_LONG).show()
                    return
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("updateFailed", "User data update to Firebase: Failed")
            }
        })
    }

    private var dialog: ProgressDialog? = null
    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    uploadImage("image1", data)
                }
                2 -> {
                    uploadImage("image2", data)
                }
                3 -> {
                    uploadImage("image3", data)
                }
                4->{
                    uploadvideo("video",data)
                }
            }
        }


    }
    private fun addDataToFirestore(field:String,uri: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Hostels")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error != null) {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    for (h: DocumentChange in value?.documentChanges!!) {

                        val h_id = h.document.getString("hostelId")
                        if (h_id != null && h_id == id) {

                            val documentRef = h.document.reference

                            documentRef.update("$field", "$uri")
                            return
                        }
                    }
                }
            })

    }
    private fun uploadImage(imageField:String,data: Intent?){
        dialog = ProgressDialog(this@ManageHostelActivity)
        dialog!!.setMessage("Uploading")

        dialog!!.show()
        imageuri = data!!.data

        var filePathAndName: String? = "HostelImages/"+"post"+System.currentTimeMillis()
        val bitmap =
            MediaStore.Images.Media.getBitmap(this@ManageHostelActivity.contentResolver, imageuri)
        val arrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            arrayOutputStream
        ) // compressing the image using bitmap

        val data: ByteArray = arrayOutputStream.toByteArray()
        val ref: StorageReference =
            FirebaseStorage.getInstance().reference.child("$filePathAndName")

        ref.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                Toast.makeText(
                    applicationContext,
                    "image updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
                dialog!!.dismiss()
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri =
                    uriTask.result.toString() // getting url if task is successful
                if (uriTask.isSuccessful) {
                    addDataToFirestore(imageField,downloadUri)


                }
            }.addOnFailureListener(OnFailureListener { })
    }

    private fun showData() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("Hostels")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred!!! ",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    for (HostelsData: DocumentChange in value?.documentChanges!!) {
                        if (HostelsData.type == DocumentChange.Type.ADDED) {
                            val h_id = HostelsData.document.getString("hostelId")
                            if (h_id != null && h_id == id) {
                                img1 = HostelsData.document.getString("image1")
                                img2 = HostelsData.document.getString("image2")
                                img3 = HostelsData.document.getString("image3")
                                val vid = HostelsData.document.getString("video")
                                if (img1!!.isNotEmpty()) {
                                    binding.dbImg1.visibility=View.VISIBLE
                                    val imageView:ImageView=binding.img1
                                    imageView.layoutParams = LinearLayout.LayoutParams (40, 40)

                                    Picasso.get().load(img1?.toUri()).into (binding.dbImg1)
                                }
                                if (img2!!.isNotEmpty()) {
                                    binding.dbImg2.visibility=View.VISIBLE
                                    val imageView:ImageView=binding.img2
                                    imageView.layoutParams = LinearLayout.LayoutParams (40, 40)
                                    Picasso.get().load(img2?.toUri()).into (binding.dbImg2)
                                }
                                if (img3!!.isNotEmpty()) {
                                    binding.dbImg3.visibility=View.VISIBLE
                                    val imageView:ImageView=binding.img3
                                    imageView.layoutParams = LinearLayout.LayoutParams (40, 40)
                                    Picasso.get().load(img3?.toUri()).into (binding.dbImg3)

                                }
                                if (vid!!.isNotEmpty()) {
                                    binding.dbVid.visibility=View.VISIBLE
                                    val imageView:ImageView=binding.video
                                    imageView.layoutParams = LinearLayout.LayoutParams (40, 40)

                                    val uri: Uri = Uri.parse(vid)
                                    binding.dbVid.setVideoURI(uri)
                                    val mediaController = MediaController(this@ManageHostelActivity)
                                    mediaController.setAnchorView(binding.dbVid)
                                    mediaController.setMediaPlayer(binding.dbVid)
                                    binding.dbVid.setMediaController(mediaController)
                                    binding.dbVid.start()


                                }
                                return
                            }
                        }
                    }
                }
            })
    }
    private fun getfiletype(videouri: Uri): String? {
        val r = contentResolver
        // get the file type ,in this case its mp4
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri))
    }

    private fun uploadvideo(videoField:String,data: Intent?) {
        progressDialog = ProgressDialog(this@ManageHostelActivity)
        progressDialog?.setTitle("Uploading...")
        progressDialog?.show()
        videouri = data?.data;
        if (videouri != null) {
            // save the selected video in Firebase storage
            val reference = FirebaseStorage.getInstance().getReference(
                "HostelVideo/" + System.currentTimeMillis() + "." + getfiletype(
                    videouri!!
                )
            )
            reference.putFile(videouri!!).addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                // get the link of video
                val downloadUri = uriTask.result.toString()
                addDataToFirestore("$videoField","$downloadUri")

                progressDialog?.dismiss()
                Toast.makeText(this@ManageHostelActivity, "Video Uploaded!!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e -> // Error, Image not uploaded
                progressDialog?.dismiss()
                Toast.makeText(this@ManageHostelActivity, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener {
                val progress: Double =
                    100.0 * it.bytesTransferred / it.totalByteCount
                progressDialog?.setMessage("Uploaded " + progress.toInt() + "%")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showData()
        showStudents()
    }

    fun studentProfileExist(newMobNo: String, callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred!!! ",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    for (userData: DocumentChange in value?.documentChanges!!) {
                        val mobileNo = userData.document.getString("mobile")
                        val profile = userData.document.getString("type")!!
                        if (mobileNo != null && mobileNo.takeLast(10) == newMobNo.takeLast(10) && profile=="student") {
                            val name = userData.document.getString("name")!!
                            callback(name)
                            return
                        }
                    }

                    // If no matching document found
                    callback("") // Return an empty string as the profile is not registered
                }
            })
    }
    private fun showStudents() {
        studentList.clear()
        val dbref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Notification")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val ds = dataSnapshot1.getValue(Notification::class.java)!!
                    val h_id=ds.h_id
                    if(h_id==id){
                        studentList.add(ds)
                    }
                    mAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }



}