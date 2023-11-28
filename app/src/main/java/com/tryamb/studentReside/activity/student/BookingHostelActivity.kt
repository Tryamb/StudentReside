package com.tryamb.studentReside.activity.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.tryamb.studentReside.appUtils.Pref
import com.tryamb.studentReside.databinding.ActivityBookingHostelBinding
import com.tryamb.studentReside.model.Notification
import org.json.JSONObject

class BookingHostelActivity : AppCompatActivity(), PaymentResultListener {
    private lateinit var binding:ActivityBookingHostelBinding
    private var id:String?=null
    private var hostelName:String?=null
    private var hostelContact:String?=null
    private var studentName:String?=null
    private val phone=Firebase.auth.currentUser?.phoneNumber

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBookingHostelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        studentName= Pref(this).getString("studentName")
        id = intent.getStringExtra("hostelId")
        hostelName = intent.getStringExtra("hostel_name")
        hostelContact = intent.getStringExtra("hostel_mobile")
        alreadyBooked(id!!, phone!!){
            if (it.isEmpty()){
                binding.msg.visibility=View.VISIBLE
                binding.proceedOnline.visibility=View.VISIBLE
            }
        }
        binding.proceedOnline.setOnClickListener {

            startRazorpayPayment()
        }

    }
    private fun startRazorpayPayment() {
        // Initialize Razorpay checkout
        val checkout = Checkout()

        checkout.setKeyID("")//This field is for key id of razorpay

        try {
            // Create a JSON object with the payment options
            val options = JSONObject()
            val purchaseDescription=intent.getStringExtra("description")
            val amountInPaise= intent.getStringExtra("amount")?.toLong()?.times(100)
            options.put("name", "Student Reside") // Name of store or company
            options.put("description", "$purchaseDescription") // Description of the payment
            options.put("currency", "INR") // Currency code (INR for Indian Rupees)
            options.put("amount", "$amountInPaise") // Amount in paise (e.g., 10000 paise = INR 100)

            // Open the Razorpay checkout activity to start the payment
            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        addRealTimeDB(id!!,phone!!,studentName!!,hostelName!!,hostelContact!!)
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(this, "Something went wrong$response",Toast.LENGTH_SHORT).show()
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
                    ref.child("status").setValue("OnlineBooked")
                    return
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("updateFailed", "Person added to hostel: Failed")
            }
        })
    }

    private fun alreadyBooked(id: String, phone: String,callback: (String) -> Unit){
        val ref = FirebaseDatabase.getInstance().getReference("Notification")
            .child(id+phone)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    val ds = dataSnapshot.getValue(Notification::class.java)!!
                    if(ds.status=="Booked" || ds.status=="OnlineBooked"){
                         binding.msg.visibility= View.GONE
                        binding.proceedOnline.visibility=View.GONE
                        binding.msgFinal.visibility=View.VISIBLE
                    }
                    return
                }
                callback("")

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }



}
