package com.tryamb.studentReside.activity.student

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.ImageActivity
import com.tryamb.studentReside.adapter.ListedPersonAdapter
import com.tryamb.studentReside.databinding.ActivityHostelDetailsBinding
import com.tryamb.studentReside.model.Notification


class HostelDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostelDetailsBinding
    private lateinit var player: SimpleExoPlayer
    private var img1:String?=null
    private var img2:String?=null
    private var img3:String?=null
    private var id:String?=null
    var mAdapter: ListedPersonAdapter? = null
    var recyclerView: RecyclerView? = null
    private lateinit var studentList: ArrayList<Notification>
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostelDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("hostelId")
        val hostelName=intent.getStringExtra("hostel_name")
        val payAmount=intent.getStringExtra("hostel_fee")
        val hostelMobile=intent.getStringExtra("hostel_mobile")
        binding.tvHostelName.text = hostelName
        binding.tvNumber.text = hostelMobile
        binding.tvHostelAddress.text = intent.getStringExtra("hostel_address")
        binding.tvHostelFee.text = "Rs. ${intent.getStringExtra("hostel_fee")}/Month"
        binding.tvHostelInTime.text = "⏰ ${intent.getStringExtra("hostel_inTime")} PM"

        binding.btnOptHostel.setOnClickListener {
            val intent=Intent(this,BookingHostelActivity::class.java)
            intent.putExtra("description","You are going to book $hostelName online")
            intent.putExtra("amount","$payAmount")
            intent.putExtra("hostelId","$id")
            intent.putExtra("hostel_name","$hostelName")
            intent.putExtra("hostel_mobile","$hostelMobile")
            startActivity(intent)
        }


        studentList = arrayListOf()
        recyclerView = findViewById(R.id.rvStd)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        mAdapter = ListedPersonAdapter(this@HostelDetailsActivity, studentList,"student")
        recyclerView!!.adapter = mAdapter

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


    }
    private fun showData() {
        val db = FirebaseFirestore.getInstance()
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
                                    binding.dbImg1.visibility= View.VISIBLE

                                    Picasso.get().load(img1?.toUri()).into (binding.dbImg1)
                                }
                                if (img2!!.isNotEmpty()) {
                                    binding.dbImg2.visibility= View.VISIBLE
                                    Picasso.get().load(img2?.toUri()).into (binding.dbImg2)
                                }
                                if (img3!!.isNotEmpty()) {
                                    binding.dbImg3.visibility= View.VISIBLE
                                    Picasso.get().load(img3?.toUri()).into (binding.dbImg3)

                                }
                                if (vid!!.isNotEmpty()) {
                                    binding.dbVid.visibility= View.VISIBLE

                                    val playerView = binding.dbVid
                                    try {
                                        player = ExoPlayerFactory.newSimpleInstance(
                                            this@HostelDetailsActivity,
                                            DefaultRenderersFactory(this@HostelDetailsActivity),
                                            DefaultTrackSelector(),
                                            DefaultLoadControl()
                                        )

                                        // Connect the player to the player view
                                        playerView.player = player

                                        val videoUri = Uri.parse(vid)
                                        val dataSourceFactory = DefaultDataSourceFactory(this@HostelDetailsActivity, "ExoPlayerDemo")
                                        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                                            .setExtractorsFactory(DefaultExtractorsFactory())
                                            .createMediaSource(videoUri)

                                        // Prepare the player with the media source
                                        player.prepare(mediaSource)
                                        player.playWhenReady = true
                                    } catch (e: Exception) {
                                        // below line is used for
                                        // handling our errors.
                                        Log.e("TAG", "Error playing in video : $e")
                                    }



                                }
                                return
                            }
                        }
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        showData()
        showStudents()
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