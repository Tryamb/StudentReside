package com.tryamb.studentReside.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tryamb.studentReside.R
import com.tryamb.studentReside.model.Tiffin

class VendorTiffinRecycler(
    private val context: Context,
    private var tiffinList: ArrayList<Tiffin>
) : RecyclerView.Adapter<VendorTiffinRecycler.TiffinViewHolder>() {

    class TiffinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rlRecyclerTiffinRow: RelativeLayout = view.findViewById(R.id.rlRecyclerTiffinRow)
        val tvTiffinCenterName: TextView = view.findViewById(R.id.tvTiffinCenterName)
        val tvFeePerMonth: TextView = view.findViewById(R.id.tvFeePerMonth)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TiffinViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_tiffin_row, parent, false)
        return TiffinViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TiffinViewHolder, position: Int) {
        val tiffin: Tiffin = tiffinList[position]
        holder.tvTiffinCenterName.text = tiffin.Name
        holder.tvFeePerMonth.text = "Rs. ${tiffin.Fee}/Month"
    }

    override fun getItemCount(): Int {
        return tiffinList.size
    }


}