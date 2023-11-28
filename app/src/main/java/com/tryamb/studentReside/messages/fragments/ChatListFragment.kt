package com.tryamb.studentReside.messages.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.tryamb.studentReside.R
import com.tryamb.studentReside.activity.student.HomeFragment
import com.tryamb.studentReside.adapter.ChatListAdapter
import com.tryamb.studentReside.model.ModelChat
import java.util.*


class ChatListFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var adapterChatList: ChatListAdapter? = null
    var dmChats: ArrayList<String>? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_chat_list, container, false)

        // getting current user
        val currentUserId=Firebase.auth.currentUser?.phoneNumber
        recyclerView = view.findViewById<RecyclerView>(R.id.chatlistrecycle)

        dmChats= arrayListOf()
        val userquery: Query = FirebaseDatabase.getInstance().getReference("ChatList")
        userquery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dmChats!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val uidFromChatList = dataSnapshot1.key.toString()
                    if ( uidFromChatList != currentUserId
                    ) {
                        dmChats!!.add(uidFromChatList)
                        Log.d("User-Id", dataSnapshot1.key.toString())
                    }
                }
                if (isAdded) { // Check if the fragment is attached to the context
                    // Create the adapter only when the fragment is attached
                    adapterChatList = ChatListAdapter(requireContext(), dmChats!!)
                    recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView!!.adapter = adapterChatList
                }

//                editText.addTextChangedListener(object : TextWatcher {
//
//                    override fun afterTextChanged(searchText: Editable?) {
//                        adapterChatList!!.filter?.filter(searchText.toString());
//                    }
//
//                    override fun beforeTextChanged(
//                        searchText: CharSequence?,
//                        start: Int,
//                        count: Int,
//                        after: Int
//                    ) {
//                    }
//
//                    override fun onTextChanged(
//                        searchText: CharSequence?,
//                        start: Int,
//                        before: Int,
//                        count: Int
//                    ) {
//                    }
//                })

                for (i in dmChats!!.indices) {
                    lastMessage(dmChats!![i])
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        setupBackPressedCallback()
        return view
    }

    private fun lastMessage(uid: String) {
        val currentUserId = Firebase.auth.currentUser?.phoneNumber
        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var userName = ""
                var lastmess = "No Messages Yet"
                var name = ""
                var time = ""
                var hisName = ""
                for (dataSnapshot1 in dataSnapshot.children) {
                    val chat =
                        dataSnapshot1.getValue(ModelChat::class.java) ?: continue
                    Log.d("Chat-Last", chat.toString())
                    val sender = chat.sender
                    val receiver = chat.receiver
                    if (sender == null || receiver == null) {
                        continue
                    }

                    // checking for the type of message if
                    // message type is image then set
                    // last message as sent a photo
                    if (chat.receiver == currentUserId && chat.sender == uid ||
                        chat.receiver == uid && chat.sender == currentUserId
                    ) {

                    if (chat.type == "images") {
                        lastmess = "Sent a Photo"

                    } else {
                        lastmess = chat.message.toString()
                    }
                        hisName=chat.name!!
                        name = chat.sender!!
                        val timeStamp = chat.timestamp
                        val calendar = Calendar.getInstance(Locale.ENGLISH)
                        calendar.timeInMillis = timeStamp!!.toLong()
                        time = DateFormat.format("d MMM hh:mm aa", calendar).toString()
                }

                }
                val triple: Triple<String, String, String> =
                    Triple(lastmess, name, time)
                adapterChatList?.setlastMessageMap(uid, triple)
                adapterChatList?.setUidToNameMap(uid, hisName)
                adapterChatList?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }


    private fun setupBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateFragment(HomeFragment())
            }
        })
    }

    private fun navigateFragment(nextFrag:Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainHolder, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()
    }



}


