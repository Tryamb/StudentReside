package com.tryamb.studentReside.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tryamb.studentReside.R
import com.tryamb.studentReside.model.StudyMaterial
import com.tryamb.studentReside.study.OpenPdfActivity

class StudyRecyclerAdapter(
    private val context: Context,
    private var studyList: ArrayList<StudyMaterial>
) :
    RecyclerView.Adapter<StudyRecyclerAdapter.StudyViewHolder>() {

    class StudyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerStudyRow: CardView = view.findViewById(R.id.cardView)
        val tvTopicName: TextView = view.findViewById(R.id.tvTopicName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.study_material_layout, parent, false)
        return StudyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        val studyMaterial: StudyMaterial = studyList[position]

        holder.tvTopicName.text = studyMaterial.topic
        holder.recyclerStudyRow.setOnClickListener {
            val url=studyMaterial.download_uri
            val intent=Intent(context,OpenPdfActivity::class.java)
            intent.putExtra("pdfUrl","$url")
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return studyList.size
    }


}