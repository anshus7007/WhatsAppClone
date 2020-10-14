package com.anshu.whatsappmessenger.ui.fragments

import android.os.Bundle
import android.os.UserHandle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.adapter.UserAdapter
import com.anshu.whatsappmessenger.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private var userAdapter:UserAdapter?=null
    private var mUsers:List<Users>?=null
    private var recyclerviewSearch: RecyclerView?=null
    private var etSearchuser: EditText?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_search, container, false)

        etSearchuser= view.findViewById(R.id.etSearchUser)
        recyclerviewSearch=view.findViewById(R.id.searchList)
        recyclerviewSearch!!.setHasFixedSize(true)
        recyclerviewSearch!!.layoutManager=LinearLayoutManager(context)
        mUsers=ArrayList()
        retrieveAllUsers()


        etSearchuser!!.addTextChangedListener(object :TextWatcher
        {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(p0.toString().toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        return view
    }

    private fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        var userRef = FirebaseDatabase.getInstance().reference.child("Users")

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if(etSearchuser!!.text.toString()=="")
                {
                    for(snap in snapshot.children)
                    {
                        var user:Users?= snap.getValue(Users::class.java)
                        if(!(user!!.getUID()).equals(firebaseUserID))
                        {
                            (mUsers as ArrayList<Users>).add(user)
                        }

                    }
                }
                userAdapter= UserAdapter(context!!,mUsers!!,false)
                recyclerviewSearch!!.adapter=userAdapter

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    private fun searchForUsers(str:String)
    {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        var queryref = FirebaseDatabase.getInstance().reference.child("Users")
            .orderByChild("search").startAt(str).endAt(str+"\uf8ff")

        queryref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for(snap in snapshot.children)
                {
                    var user:Users?= snap.getValue(Users::class.java)
                    if(!(user!!.getUID()).equals(firebaseUserID))
                    {
                        (mUsers as ArrayList<Users>).add(user)
                    }

                }
                userAdapter= UserAdapter(context!!,mUsers!!,false)
                recyclerviewSearch!!.adapter=userAdapter

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}