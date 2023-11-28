package com.tryamb.studentReside.adapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import  com.tryamb.studentReside.R
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tryamb.studentReside.model.ModelChat
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class AdapterChat(var context: Context, var list: List<ModelChat>, var name:String) :
    RecyclerView.Adapter<AdapterChat.Myholder>() {
    var imageurl: String? = null
    val myuid: String? =Firebase.auth.currentUser?.phoneNumber
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myholder {
        return if (viewType == MSG_TYPE_LEFT) {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false)
            Myholder(view)
        } else {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false)
            Myholder(view)
        }
    }

    override fun onBindViewHolder(holder: Myholder, @SuppressLint("RecyclerView") position: Int) {
        val message: String = list[position].message!!
        val timeStamp: String = list[position].timestamp!!
        val name: String = list[position].name!!
        val uid: String = list[position].sender!!
        val type: String = list[position].type!!
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timeStamp.toLong()
        val time = DateFormat.format("hh:mm aa", calendar).toString()
        Log.d("Boolean", position.toString())
        if (position == 0) {
            holder.mDate.text = getDate(list[position].timestamp!!)
        } else {
            val currentDate = getDate(list[position].timestamp!!)
            val previousDate = getDate(list[position - 1].timestamp!!)
            if (currentDate != previousDate) {
                holder.mDate.text = currentDate
            } else {
                holder.mDate.visibility = View.GONE
            }
        }
        holder.message.text = message
        holder.name.text = name
        holder.time.text = time
        try {
            Picasso.get().load(imageurl).into(holder.image)
        } catch (e: Exception) {
        }
        if (type == "text") {
            holder.message.visibility = View.VISIBLE
            holder.mimage.visibility = View.GONE
            holder.message.text = message
        } else {
            holder.message.visibility = View.GONE
            holder.mimage.visibility = View.VISIBLE
            Picasso.get().load(message).into(holder.mimage)
        }
        holder.msglayput.setOnClickListener {
            val builder = AlertDialog.Builder(
                context
            )
            val inflater = LayoutInflater.from(context)
            val dialogView: View =
                inflater.inflate(R.layout.custom_dialog_layout, null)
            builder.setView(dialogView)
            val deleteButton =
                dialogView.findViewById<Button>(R.id.dialog_delete_button)
            val cancelButton =
                dialogView.findViewById<Button>(R.id.dialog_cancel_button)
            val dialog = builder.create()
            dialog.show()
            deleteButton.setOnClickListener {
                deleteMsg(position)
                notifyDataSetChanged()
                dialog.dismiss()
            }
            cancelButton.setOnClickListener { dialog.dismiss() }
        }

    }

    private fun deleteMsg(position: Int) {
        val msgtimestmp: String = list[position].timestamp!!
        val dbref = FirebaseDatabase.getInstance().reference.child("Chats")
        val query = dbref.orderByChild("timestamp").equalTo(msgtimestmp)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    if (dataSnapshot1.child("sender").value == myuid) {
                        // any two of below can be used
                        dataSnapshot1.ref.removeValue()

                    } else {
                        Toast.makeText(
                            context,
                            "you can delete only your msg....",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = list[position].timestamp!!.toLong()
        val date = DateFormat.format("yyyy-MM-dd", calendar).toString()
        return if (list[position].sender.equals(myuid)) {
            MSG_TYPR_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

  inner class Myholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: CircleImageView? = null
        var mimage: ImageView
        var message: TextView
        var time: TextView
        var mDate: TextView
        var isSee: TextView
        var name: TextView
        var msglayput: RelativeLayout

        init {
            message = itemView.findViewById(R.id.msgc)
            time = itemView.findViewById(R.id.timetv)
            mDate = itemView.findViewById(R.id.date)
            isSee = itemView.findViewById(R.id.isSeen)
            msglayput = itemView.findViewById(R.id.msglayout)
            mimage = itemView.findViewById(R.id.images)
            name = itemView.findViewById(R.id.fullName)
        }
    }

    private fun getDate(timeStamp: String): String {
        val calendar =
            Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = timeStamp.toLong()
        return DateFormat.format("yyyy-MM-dd", calendar).toString()
    }

    companion object {
        private const val MSG_TYPE_LEFT = 0
        private const val MSG_TYPR_RIGHT = 1
    }
}

