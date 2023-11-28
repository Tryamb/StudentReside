package com.tryamb.studentReside.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.messages.ChatActivity
import com.tryamb.studentReside.model.Notification

class ListedPersonAdapter(
    private val context: Context,
    private var hList: ArrayList<Notification>,
    private val profile:String
) : RecyclerView.Adapter<ListedPersonAdapter.PersonViewHolder>()  {
    private var currPhone=Firebase.auth.currentUser?.phoneNumber
    class PersonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.s_Name)
        val tvPhone: TextView = view.findViewById(R.id.s_phone)
        val messageBtn:Button=view.findViewById(R.id.btnMessage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_students, parent, false)
        return PersonViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val n: Notification = hList[position]
        holder.tvName.text=n.name
        holder.tvPhone.text=n.phone


        if(profile=="student" && n.phone!=currPhone ){
            holder.messageBtn.visibility=View.VISIBLE
            holder.messageBtn.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("receiver_id", n.phone)
                intent.putExtra("name", n.name)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return hList.size
    }


}