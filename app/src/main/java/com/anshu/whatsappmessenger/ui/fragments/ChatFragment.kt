package com.anshu.whatsappmessenger.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.adapter.UserAdapter
import com.anshu.whatsappmessenger.model.ChatLists
import com.anshu.whatsappmessenger.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {

    private var userAdapter: UserAdapter?=null
    private var mUsers:List<Users>?=null
    private var userChatList: List<ChatLists>?=null
    lateinit var recycler_view_chatLists:RecyclerView
    private var firebaseUser:FirebaseUser?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_chat, container, false)
        recycler_view_chatLists=view.findViewById(R.id.recycler_view_chat)
        recycler_view_chatLists.setHasFixedSize(true)
        recycler_view_chatLists.layoutManager=LinearLayoutManager(context)

        firebaseUser=FirebaseAuth.getInstance().currentUser
        userChatList=ArrayList()

        val ref= FirebaseDatabase.getInstance().reference.child("ChatLists").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (userChatList as ArrayList).clear()
                for(dataSnapShot in snapshot.children)
                {
                    val chatList=dataSnapShot.getValue(ChatLists::class.java)
                    (userChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        return view
    }


    private fun retrieveChatList()
    {
        mUsers=ArrayList()
        val ref= FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (mUsers as ArrayList).clear()
                for(dataSnapShot in snapshot.children)
                {
                    val user=dataSnapShot.getValue(Users::class.java)
                    for(eachChatList in userChatList!!)
                    {
                        if(user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user!!)

                        }
                    }
                }
                userAdapter=UserAdapter(context!!,(mUsers as ArrayList<Users>),true)

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}