package com.anshu.whatsappmessenger.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.user_item_search_layout.view.*

class SettingsFragment : Fragment() {

    var userReference:DatabaseReference?=null
    var firebaseUser:FirebaseUser?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser=FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        userReference!!.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user: Users?=snapshot.getValue(Users::class.java)
                    if(context!=null)
                    {
                        view.username_settings.text=user!!.getUserName()
                        Picasso.get().load(user!!.getProfile()).into(profile_image_settings)
                        Picasso.get().load(user!!.getCover()).into(cover_image)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        return view
    }



}