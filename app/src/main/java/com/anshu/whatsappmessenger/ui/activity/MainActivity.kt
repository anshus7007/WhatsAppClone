package com.anshu.whatsappmessenger.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.DialogTitle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Chat
import com.anshu.whatsappmessenger.model.Users
import com.anshu.whatsappmessenger.ui.fragments.ChatFragment
import com.anshu.whatsappmessenger.ui.fragments.SearchFragment
import com.anshu.whatsappmessenger.ui.fragments.SettingsFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference?=null
    var firebaseUser: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        firebaseUser=FirebaseAuth.getInstance().currentUser
        refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar:Toolbar= findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val tabLayout:TabLayout= findViewById(R.id.tab_layout)
        val viewPager:ViewPager=findViewById(R.id.view_pager)

        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val viewPagerAdapter =ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages=0
                for(dataSnapShot in snapshot.children)
                {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(firebaseUser!!.uid)&&!chat!!.getIsSeen())
                    {
                        countUnreadMessages+=1
                    }
                }
                if(countUnreadMessages==0)
                {
                    viewPagerAdapter.addFragment(ChatFragment(),"Chats")
                }
                else
                {
                    viewPagerAdapter.addFragment(ChatFragment(),"($countUnreadMessages) Chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(),"Search")
                viewPagerAdapter.addFragment(SettingsFragment(),"Settings")
                viewPager.adapter=viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        //display username and profile pictues

        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user: Users?=snapshot.getValue(Users::class.java)
                    user_name.text=user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         when (item.itemId) {
            R.id.action_signOut ->
            {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,LoginActivity::class.java))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
                return true
            }
        }
        return false
    }
    class ViewPagerAdapter(fragmentManager:FragmentManager):FragmentPagerAdapter(fragmentManager)
    {

        private val fragments:ArrayList<Fragment>
        private val titles: ArrayList<String>
                init
                {
                    fragments=ArrayList<Fragment>()
                    titles=ArrayList<String>()
                }
        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment:Fragment,title: String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

    }

    private fun updateStatus(status:String)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap=HashMap<String,Any>()
        hashMap["status"]=status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }
}