package com.tryamb.studentReside.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.student.TiffinDetailsActivity
import com.tryamb.studentReside.adapter.TiffinRecyclerAdapter.*
import com.tryamb.studentReside.model.Tiffin

class TiffinRecyclerAdapter(
    private val context: Context,
    private var tiffinList: ArrayList<Tiffin>
) : RecyclerView.Adapter<TiffinViewHolder>() {

    class TiffinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rlRecyclerTiffinRow: RelativeLayout = view.findViewById(R.id.rlRecyclerTiffinRow)
        val tvTiffinCenterName: TextView = view.findViewById(R.id.tvTiffinCenterName)
        val tvFeePerMonth: TextView = view.findViewById(R.id.tvFeePerMonth)
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

        holder.rlRecyclerTiffinRow.setOnClickListener {
            val intent = Intent(context, TiffinDetailsActivity::class.java)
            intent.putExtra("tiffin_name", tiffin.Name)
            intent.putExtra("tiffin_mobile", tiffin.Mobile)
            intent.putExtra("tiffin_address", tiffin.Address)
            intent.putExtra("tiffin_fee", tiffin.Fee)
            intent.putExtra("tiffin_rating", tiffin.Rating)
            context.startActivity(intent)
        }

        holder.tvTiffinCenterName.text = tiffin.Name
        holder.tvFeePerMonth.text = "Rs. ${tiffin.Fee}/Month"
    }

    override fun getItemCount(): Int {
        return tiffinList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: ArrayList<Tiffin>) {
        tiffinList = filteredList
        notifyDataSetChanged()
    }

}