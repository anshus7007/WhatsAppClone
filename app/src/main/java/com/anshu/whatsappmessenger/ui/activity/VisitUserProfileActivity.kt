package com.anshu.whatsappmessenger.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {

    private var userIdVisit:String=""
    var user:Users?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        userIdVisit=intent.getStringExtra("visit_id").toString()
        val ref=FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit)


        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                     user=snapshot.getValue(Users::class.java)
                    username_display.text=user!!.getUserName()
                    Picasso.get().load(user!!.getProfile()).into(profile_display)
                    Picasso.get().load(user!!.getCover()).into(cover_display)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        facebook_display.setOnClickListener {
            val uri=Uri.parse(user!!.getFaceBook())
            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        instagram_display.setOnClickListener {
            val uri=Uri.parse(user!!.getInstagram())
            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        website_display.setOnClickListener {
            val uri=Uri.parse(user!!.getInstagram())
            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
        send_msg.setOnClickListener {
            val intent=Intent(this,MessageChatActivity::class.java)
            intent.putExtra("visit_id",user!!.getUID())
            startActivity(intent)
        }
    }
}