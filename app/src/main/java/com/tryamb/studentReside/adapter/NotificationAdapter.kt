package com.tryamb.studentReside.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.model.Notification

class NotificationAdapter(
    private val context: Context,
    private var hList: ArrayList<Notification>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>()  {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAccept: TextView = view.findViewById(R.id.accept)
        val tvIgnore: TextView = view.findViewById(R.id.ignore)
        val tvName: TextView = view.findViewById(R.id.hostelName)
        val tvPhone: TextView = view.findViewById(R.id.h_phone)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_notification, parent, false)
        return NotificationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val n: Notification = hList[position]
        holder.tvName.text=n.h_name
        holder.tvPhone.text=n.h_phone
        if(n.status=="Booked"){
            holder.tvAccept.visibility=View.GONE
            holder.tvIgnore.visibility=View.GONE
            holder.status.visibility=View.VISIBLE
        }
        else if(n.status=="OnlineBooked"){
            holder.tvAccept.visibility=View.GONE
            holder.tvIgnore.visibility=View.GONE
            holder.status.visibility=View.VISIBLE
            holder.status.text="You made payment and opted this."
        }
        holder.tvAccept.setOnClickListener{
            //Toast.makeText(context,"${n.phone}+${n.h_id}",Toast.LENGTH_SHORT).show()
            updateStatus(n.h_id!!,n.phone!!)
        }
        holder.tvIgnore.setOnClickListener{
            delete(n.h_id!!,n.phone!!)
        }
    }

    override fun getItemCount(): Int {
        return hList.size
    }

    private fun updateStatus(id:String,phone:String){
       val db = FirebaseDatabase.getInstance()
        val childRef = db!!.getReference("Notification").child(id+phone)
        val updates: HashMap<String, Any> = HashMap()
        updates["status"] = "Booked"
        childRef.updateChildren(updates)

    }
    private fun delete(id:String,phone:String){
        val db = FirebaseDatabase.getInstance()
        val childRef = db!!.getReference("Notification").child(id+phone)
        childRef.removeValue()

    }


}