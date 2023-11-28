package com.tryamb.studentReside.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tryamb.studentReside.activity.student.HostelDetailsActivity
import com.tryamb.studentReside.R
import com.tryamb.studentReside.model.Hostel

class HostelRecyclerAdapter(
    private val context: Context,
    private var hostelList: ArrayList<Hostel>
) :
    RecyclerView.Adapter<HostelRecyclerAdapter.HostelViewHolder>() {

    class HostelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rlRecyclerHostelRow: LinearLayout = view.findViewById(R.id.rlRecyclerHostelRow)
        val tvHostelName: TextView = view.findViewById(R.id.tvHostelName)
        val imgHostelType: ImageView = view.findViewById(R.id.imgHostelType)
        val tvFeePerMonth: TextView = view.findViewById(R.id.tvFeePerMonth)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val hostelDetail: TextView = view.findViewById(R.id.tvDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_hostel_row, parent, false)
        return HostelViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HostelViewHolder, position: Int) {
        val hostel: Hostel = hostelList[position]

        holder.rlRecyclerHostelRow.setOnClickListener {
            val intent = Intent(context, HostelDetailsActivity::class.java)
            intent.putExtra("hostel_name", hostel.Name)
            intent.putExtra("hostelId", hostel.hostelId)
            intent.putExtra("hostel_mobile", hostel.Mobile)
            intent.putExtra("hostel_address", hostel.Address)
            intent.putExtra("hostel_fee", hostel.Fee)
            intent.putExtra("hostel_inTime", hostel.Time)
            intent.putExtra("hostel_rating", hostel.Rating)
            context.startActivity(intent)
        }


        //------hostel name-----
        holder.tvHostelName.text = hostel.Name

        //------hostel type------
        if (hostel.Type == "Girls") {
            holder.imgHostelType.setImageResource(R.drawable.girl32)
        } else {
            holder.imgHostelType.setImageResource(R.drawable.boy32)
        }

        //---------- hostel price per month-----------
        holder.tvFeePerMonth.text = "Rs. ${hostel.Fee}/Month"


        //---------- hostel details-----------
        var allDetails=""
        for(i in hostel.Detail!!){
            allDetails+= "*$i   "
        }
        holder.hostelDetail.text= "$allDetails"

    }

    override fun getItemCount(): Int {
        return hostelList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: ArrayList<Hostel>) {
        hostelList = filteredList
        notifyDataSetChanged()
    }

}