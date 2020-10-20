package com.anshu.whatsappmessenger.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Chat
import com.anshu.whatsappmessenger.model.Users
import com.anshu.whatsappmessenger.ui.activity.MessageChatActivity
import com.anshu.whatsappmessenger.ui.activity.VisitUserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(context:Context, mUsers:List<Users>,
isThatCheck: Boolean):RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{

    private var context=context
    private var mUser=mUsers
    private var isThatCheck= isThatCheck
    var lastMsg:String=""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item_search_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var user= mUser[position]
        holder.usernameSearch.text=user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImage)

        if(isThatCheck)
        {
            retrieveMessage(user.getUID(),holder.lastMessage)

        }
        else
        {
            holder.lastMessage.visibility=View.GONE
        }
        if(isThatCheck)
        {
            if(user.getStatus()=="online")
            {
                holder.imageOnline.visibility=View.VISIBLE
                holder.imageOffline.visibility=View.GONE
            }
            else{
                holder.imageOnline.visibility=View.GONE
                holder.imageOffline.visibility=View.VISIBLE
            }
        }
        else
        {
            holder.imageOnline.visibility=View.GONE
            holder.imageOffline.visibility=View.GONE
        }
        holder.itemView.setOnClickListener {
            val options= arrayOf<CharSequence>("Send message","View Profile")

            val builder:AlertDialog.Builder=AlertDialog.Builder(context)
            builder.setTitle("What do you want?")
            builder.setItems(options,DialogInterface.OnClickListener{
                dialog,position->
                if(position==0)
                {
                    val intent = Intent(context,MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    context.startActivity(intent)
                }
                if (position==1)
                {
                    val intent = Intent(context, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    context.startActivity(intent)
                }
            })
            builder.show()
        }
    }

    private fun retrieveMessage(chatUserId: String?, lastMessageTxt: TextView) {

        lastMsg="defaultMessage"

        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnaphot in snapshot.children)
                {
                    val chat: Chat?=dataSnaphot.getValue(Chat::class.java)
                    if(firebaseUser!=null&&chat!=null)
                    {
                        if(chat.getReceiver()==firebaseUser!!.uid&&chat.getSender()==chatUserId||
                                chat.getReceiver()==chatUserId&&chat.getSender()==firebaseUser!!.uid)
                        {
                            lastMsg=chat.getMessage()!!
                        }
                    }
                }
                when(lastMsg)
                {
                    "defaultMessage"->lastMessageTxt.text=""
                    "sent you an image"->lastMessageTxt.text="image sent"
                    else-> lastMessageTxt.text=lastMsg
                }
                lastMsg="defaultMessage"
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class UserViewHolder(item: View): RecyclerView.ViewHolder(item)
    {
         var profileImage: CircleImageView=item.findViewById(R.id.profile_image_search)
         var usernameSearch:TextView= item.findViewById(R.id.usernameSearch)
        var imageOnline: CircleImageView=item.findViewById(R.id.image_online)
        var imageOffline: CircleImageView=item.findViewById(R.id.image_offline)
        var lastMessage: TextView=item.findViewById(R.id.message_last)

    }
}