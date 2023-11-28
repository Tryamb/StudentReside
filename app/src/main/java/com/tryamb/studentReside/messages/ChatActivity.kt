package com.tryamb.studentReside.messages
import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.tryamb.studentReside.adapter.AdapterChat
import com.tryamb.studentReside.R
import com.tryamb.studentReside.model.ModelChat
import java.io.ByteArrayOutputStream
import java.io.IOException

class ChatActivity : AppCompatActivity() {
    var toolbar: Toolbar? = null
    var recyclerView: RecyclerView? = null
    var msg: EditText? = null
    var personName: TextView? = null
    var send: ImageButton? = null
    var attach: ImageButton? = null
    var uid: String? = null
    var myuid: String? = null
    var personname: String? = null
    var valueEventListener: ValueEventListener? = null
    var chatList: MutableList<ModelChat>? = null
    var adapterChat: AdapterChat? = null
    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>
    var imageuri: Uri? = null
    var firebaseDatabase: FirebaseDatabase? = null
    var users: DatabaseReference? = null
    var notify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        uid = intent.getStringExtra("receiver_id")
        personname = intent.getStringExtra("name")

        msg = findViewById<EditText>(R.id.messaget)
        personName = findViewById<TextView>(R.id.nameptv)
        personName!!.text = personname
        send = findViewById<ImageButton>(R.id.sendmsg)
        attach = findViewById<ImageButton>(R.id.attachbtn)
        if (recyclerView == null) {
            Toast.makeText(this, "Chats loading", Toast.LENGTH_SHORT).show()
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView = findViewById<RecyclerView>(R.id.chatrecycle)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = linearLayoutManager
        myuid = Firebase.auth.currentUser?.phoneNumber

        // getting uid of another user using intent
        firebaseDatabase = FirebaseDatabase.getInstance()

        // initialising permissions
        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //checkUserStatus();
        users = firebaseDatabase!!.getReference("Users")
        attach!!.setOnClickListener(View.OnClickListener { showImagePicDialog() })
        send!!.setOnClickListener(View.OnClickListener {
            notify = true
            val message = msg!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(message)) { //if empty
                Toast.makeText(this@ChatActivity, "Please Write Something Here", Toast.LENGTH_LONG)
                    .show()
            } else {
                sendmessage(message)
            }
            msg!!.setText("")
        })
        readMessages()
    }

    private fun showImagePicDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Image From")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                if (!checkCameraPermission()) { // if permission is not given
                    requestCameraPermission() // request for permission
                } else {
                    pickFromCamera() // if already access granted then click
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) { // if permission is not given
                    requestStoragePermission() // request for permission
                } else {
                    pickFromGallery() // if already access granted then pick
                }
            }
        }
        builder.create().show()
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermission, CAMERA_REQUEST)
        }
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST)
    }

    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageuri =
            this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val camerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri)
        startActivityForResult(camerIntent, IMAGE_PICKCAMERA_REQUEST)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(storagePermission, STORAGE_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // request for permission if not given
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromCamera() // if access granted then click
                    } else {
                        Toast.makeText(
                            this,
                            "Please Enable Camera and Storage Permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST -> {
                if (grantResults.size > 0) {
                    val writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (writeStorageaccepted) {
                        pickFromGallery() // if access granted then pick
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageuri = data!!.data // get image data to upload
                try {
                    sendImageMessage(imageuri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
                try {
                    sendImageMessage(imageuri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(IOException::class)
    private fun sendImageMessage(imageuri: Uri?) {
        notify = true
        val dialog = ProgressDialog(this)
        dialog.setMessage("Sending Image")
        dialog.show()

        // If we are sending image as a message
        // then we need to find the url of
        // image after uploading the
        // image in firebase storage
        val timestamp = "" + System.currentTimeMillis()
        val filepathandname = "ChatImages/post$timestamp" // filename
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageuri)
        val arrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            arrayOutputStream
        ) // compressing the image using bitmap
        val data = arrayOutputStream.toByteArray()
        val ref = FirebaseStorage.getInstance().reference.child(filepathandname)
        ref.putBytes(data).addOnSuccessListener { taskSnapshot ->
            dialog.dismiss()
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result.toString() // getting url if task is successful
            if (uriTask.isSuccessful) {
                val re = FirebaseDatabase.getInstance().reference
                val hashMap = HashMap<String, Any?>()
                hashMap["sender"] = myuid
                hashMap["receiver"] = uid
                hashMap["message"] = downloadUri
                hashMap["timestamp"] = timestamp
                hashMap["type"] = "images"
                hashMap["name"] =  personname
                re.child("Chats").push().setValue(hashMap) // push in firebase using unique id
                val ref1 = FirebaseDatabase.getInstance().getReference("ChatList").child(
                    uid!!
                ).child(myuid!!)
                ref1.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            ref1.child("id").setValue(myuid)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
                val ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(
                    myuid!!
                ).child(uid!!)
                ref2.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            ref2.child("id").setValue(uid)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }.addOnFailureListener { }
    }

    private fun readMessages() {
        // show message after retrieving data
        chatList = arrayListOf()
        val dbref = FirebaseDatabase.getInstance().reference.child("Chats")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelChat: ModelChat? = dataSnapshot1.getValue(ModelChat::class.java)
                    Log.d("Sender", modelChat?.sender!!)
                    Log.d("Receiver", modelChat?.receiver!!)
                    if (modelChat?.sender!! == myuid &&
                        modelChat?.receiver!! == uid ||
                        modelChat?.receiver!! == myuid
                        && modelChat?.sender!! == uid
                    ) {
                        chatList!!.add(modelChat)
                    }

                    if (adapterChat == null) {
                        adapterChat = AdapterChat(this@ChatActivity,
                            chatList as ArrayList<ModelChat>,personname!!
                        )
                        recyclerView!!.adapter = adapterChat
                    } else {
                        adapterChat!!.notifyDataSetChanged()
                        recyclerView!!.adapter = adapterChat
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendmessage(message: String) {
        // creating a reference to store data in firebase
        // We will be storing data using current time in "Chatlist"
        // and we are pushing data using unique id in "Chats"
        val databaseReference = FirebaseDatabase.getInstance().reference
        val timestamp = System.currentTimeMillis().toString()
        val hashMap = HashMap<String, Any?>()
        hashMap["sender"] = myuid
        hashMap["receiver"] = uid
        hashMap["message"] = message
        hashMap["timestamp"] = timestamp
        hashMap["type"] = "text"
        hashMap["name"] =  personname
        databaseReference.child("Chats").push().setValue(hashMap)
        val ref1 = FirebaseDatabase.getInstance().getReference("ChatList").child(
            uid!!
        ).child(myuid!!)
        ref1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref1.child("id").setValue(myuid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(
            myuid!!
        ).child(uid!!)
        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    ref2.child("id").setValue(uid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object {
        private const val IMAGEPICK_GALLERY_REQUEST = 300
        private const val IMAGE_PICKCAMERA_REQUEST = 400
        private const val CAMERA_REQUEST = 100
        private const val STORAGE_REQUEST = 200
    }
}