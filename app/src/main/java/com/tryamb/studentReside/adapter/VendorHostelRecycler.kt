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
import com.tryamb.studentReside.R
import com.tryamb.studentReside.model.Hostel
import com.tryamb.studentReside.vendor.ManageHostelActivity

class VendorHostelRecycler(
    private val context: Context,
    private var hostelList: ArrayList<Hostel>
) :
    RecyclerView.Adapter<VendorHostelRecycler.HostelViewHolder>() {

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
            val intent = Intent(context, ManageHostelActivity::class.java)
            intent.putExtra("id",hostel.hostelId)
            intent.putExtra("hostelName",hostel.Name)
            intent.putExtra("hostelContact",hostel.Mobile)
            context.startActivity(intent)
        }

        //---------- hostel image-----------
//        Picasso.get().load(hostel.Image).error(R.drawable.hostelmateicon)
//            .into(holder.imgHostelImage)

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


}