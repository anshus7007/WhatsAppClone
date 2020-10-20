package com.anshu.whatsappmessenger.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Chat
import com.anshu.whatsappmessenger.ui.activity.ViewFullImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(context: Context, mChatList: List<Chat>, imageUrl:String):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder?>()
{

    private val context:Context
    private var mChatList:List<Chat>
    private val imageUrl:String
    var firebaseUser:FirebaseUser?=FirebaseAuth.getInstance().currentUser!!

    init {
        this.context=context
        this.mChatList=mChatList
        this.imageUrl=imageUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if(viewType==1)
        {
            val view = LayoutInflater.from(context).inflate(R.layout.message_item_right_layout,parent,false)
             ChatViewHolder(view)
        }
        else
        {
            val view = LayoutInflater.from(context).inflate(R.layout.message_item_left_layout,parent,false)
            ChatViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val chat:Chat=mChatList[position]

        Picasso.get().load(imageUrl).into(holder.chat_profile_image)

            //image-processing
            if(chat.getMessage().equals("sent you an image")&&!chat.getUrl().equals(""))
            {
                //image message-> right sender side
                if(chat.getSender().equals(firebaseUser!!.uid))
                {
                    holder.show_text_message!!.visibility=View.GONE
                    holder.right_image_view!!.visibility=View.VISIBLE
                    Picasso.get().load(chat.getUrl()).into(holder.right_image_view)


                    holder.right_image_view!!.setOnClickListener {
                        val options= arrayOf<CharSequence>("View full image",
                        "Delete message",
                        "Cancel"
                        )

                        val builder: AlertDialog.Builder= AlertDialog.Builder(context)
                        builder.setTitle("What do you want?")
                        builder.setItems(options, DialogInterface.OnClickListener{
                                dialog,which->
                            if(which==0)
                            {
                                val intent = Intent(context, ViewFullImage::class.java)
                                intent.putExtra("url",chat.getUrl())
                                context.startActivity(intent)
                            }
                            else if(which==1)
                            {
                                deleteSentMessage(position,holder)
                            }
                        })
                        builder.show()
                    }
                }
                else if(!chat.getSender().equals(firebaseUser!!.uid))
                //image message-> left receiver side
                {
                    holder.show_text_message!!.visibility=View.GONE
                    holder.left_image_view!!.visibility=View.VISIBLE
                    Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

                    holder.left_image_view!!.setOnClickListener {
                        val options= arrayOf<CharSequence>("View full image",
                        "Cancel")

                        val builder: AlertDialog.Builder= AlertDialog.Builder(context)
                        builder.setTitle("What do you want?")
                        builder.setItems(options, DialogInterface.OnClickListener{
                                dialog,which->
                            if(which==0)
                            {
                                val intent = Intent(context, ViewFullImage::class.java)
                                intent.putExtra("url",chat.getUrl())
                                context.startActivity(intent)
                            }

                        })
                        builder.show()
                    }
                }
            }
            //chat-processing
            else
            {
                holder.show_text_message!!.text=chat.getMessage()

                if(firebaseUser!!.uid==chat.getSender())
                {
                    holder.show_text_message!!.setOnClickListener {
                        val options= arrayOf<CharSequence>(
                        "Delete message",
                        "Cancel")

                        val builder: AlertDialog.Builder= AlertDialog.Builder(context)
                        builder.setTitle("What do you want?")
                        builder.setItems(options, DialogInterface.OnClickListener{
                                dialog,which->

                            if(which==0)
                            {
                                deleteSentMessage(position,holder)
                            }
                        })
                        builder.show()
                    }
                }
            }

            //sent and seen messages
            if(position==mChatList.size-1)
            {
                if(chat.getIsSeen())
                {
                    holder.textSeen!!.text="Seen"
                    if(chat.getMessage().equals("sent you an image")&&!chat.getUrl().equals(""))
                    {
                            val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                            lp!!.setMargins(0,245,10,0)
                        holder.textSeen!!.layoutParams=lp
                    }
                }
                else
                {
                    holder.textSeen!!.text="Sent"
                    if(chat.getMessage().equals("sent you an image")&&!chat.getUrl().equals(""))
                    {
                        val lp: RelativeLayout.LayoutParams? = holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                        lp!!.setMargins(0,245,10,0)
                        holder.textSeen!!.layoutParams=lp
                    }
                }

            }
            else
            {
                holder.textSeen!!.visibility=View.GONE
            }


    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    class ChatViewHolder(item: View):RecyclerView.ViewHolder(item)
    {
            var chat_profile_image:CircleImageView?=null
            var show_text_message:TextView?=null
            var left_image_view: ImageView?=null
            var textSeen: TextView?=null
            var right_image_view:ImageView?=null

            init {
                chat_profile_image=item.findViewById(R.id.message_profile)
                show_text_message=item.findViewById(R.id.show_text_message)
                left_image_view=item.findViewById(R.id.left_image_view)
                textSeen=item.findViewById(R.id.text_seen)
                right_image_view=item.findViewById(R.id.right_image_view)

            }
    }

    override fun getItemViewType(position: Int): Int {
        return if(mChatList[position].getSender().equals(firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }
    }
    private fun deleteSentMessage(position:Int,holder:ChatAdapter.ChatViewHolder)
    {
        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList[position].getMessageId()!!)
            .removeValue()
            .addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    Toast.makeText(holder.itemView.context,"Deleted",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(holder.itemView.context,"Failed to Delete",Toast.LENGTH_SHORT).show()

                }
            }
    }
}