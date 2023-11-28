package com.tryamb.studentReside.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.messages.ChatActivity
import java.util.*


class ChatListAdapter(context: Context, chatList: ArrayList<String>) :
    RecyclerView.Adapter<ChatListAdapter.Myholder>(),Filterable{
    private val yourUid=Firebase.auth.currentUser?.phoneNumber
    var context: Context
    var chatList:ArrayList<String>
    private val chatListFull: ArrayList<String>

    private val lastMessageMap: HashMap<String, Triple<String, String, String>?> = HashMap()
    private val userNameMap: HashMap<String, String> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myholder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false)
        return Myholder(view)
    }

    override fun onBindViewHolder(holder: Myholder, position: Int) {
        val hisuid = chatList[position]
        val personName = userNameMap[hisuid]
        val lastmess: Triple<String, String, String>? = lastMessageMap[hisuid]
        holder.name.text = personName

        // holder.msgTime.setText(time);
        if (lastMessageMap[hisuid] == null) {
            // if no last message then Hide the layout
            holder.lastmessage.visibility = View.GONE
            holder.senderName.visibility = View.GONE
            holder.msgTime.visibility = View.GONE
        } else {
            val lastmessage = lastmess!!.component1() as String
            val senderUid = lastmess.component2() as String
            val msgTime = lastmess.component3() as String
            holder.lastmessage.visibility = View.VISIBLE
            holder.lastmessage.text = lastmessage
            holder.msgTime.visibility = View.VISIBLE
            holder.msgTime.text = msgTime

            if(senderUid==yourUid){
                holder.senderName.text="You:"
            }
        }

        // redirecting to chat activity on item click
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("receiver_id", hisuid)
            intent.putExtra("name", personName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun setlastMessageMap(userId: String, lastmessage: Triple<String, String, String>?) {
        lastMessageMap[userId] = lastmessage
    }

    fun setUidToNameMap(userId: String, name: String) {
        userNameMap[userId] = name
    }


    init {
        this.context = context
        this.chatList=chatList
        this.chatListFull = chatList
        this.chatList = ArrayList(chatListFull)
    }

    inner class Myholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile: ImageView
        var name: TextView
        var lastmessage: TextView
        var senderName: TextView
        var msgTime: TextView

        init {
            profile = itemView.findViewById(R.id.profileimage)
            name = itemView.findViewById(R.id.nameonline)
            lastmessage = itemView.findViewById(R.id.lastmessge)
            senderName = itemView.findViewById(R.id.nameSender)
            msgTime = itemView.findViewById(R.id.msgTime)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: ArrayList<String>) {
        chatList = filteredList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter? {
        return chatFilter
    }

    private val chatFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
           // Toast.makeText(context,"filter process",Toast.LENGTH_LONG).show()
            val filteredChatList: ArrayList<String> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredChatList.addAll(chatListFull)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }

                for (userId in chatListFull) {
                    val username = userNameMap[userId]
                    if (username!!.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredChatList.add(userId)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredChatList
            results.count = filteredChatList.size
            Toast.makeText(context,"${filteredChatList.size}",Toast.LENGTH_LONG).show()

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            chatList.clear()
           chatList.addAll(results.values as ArrayList<String>)
            notifyDataSetChanged()
        }
    }

}


