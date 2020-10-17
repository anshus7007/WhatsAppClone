package com.anshu.whatsappmessenger.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit:String? = ""
    var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        intent=intent
        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser= FirebaseAuth.getInstance().currentUser

        val reference=FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit!!)
        reference.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user:Users?= snapshot.getValue(Users::class.java)
                username_mchat.text=user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_chat)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        send_message_btn.setOnClickListener {
            val message= text_message.text.toString()
            if(message=="")
            {
                Toast.makeText(this,"Write message first",Toast.LENGTH_SHORT).show()
            }
            else
            {
                sendmessageToUser(firebaseUser!!.uid,userIdVisit,message)
            }

            text_message.setText("")
        }

        attach_image_file.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),438)


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==438&&resultCode== RESULT_OK&&data!=null&&data!!.data!=null)
        {
            val progressBar=ProgressDialog(this)
            progressBar.setMessage("Image is uploading....")
            progressBar.show()
            val fileUri=data.data
            val storageReference=FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref= FirebaseDatabase.getInstance().reference
            val messageId= ref.push().key
            val filePath=storageReference.child("$messageId.jpg")


            var uploadTask: StorageTask<*>
            uploadTask=filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task->
                if(!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener{ task->
                if(task.isSuccessful)
                {
                    var downloadUrl =task.result
                    val url = downloadUrl.toString()


                    val messagehashMap=HashMap<String,Any?>()
                    messagehashMap["sender"]=firebaseUser!!.uid
                    messagehashMap["message"]="sent you an image"
                    messagehashMap["receiver"]=userIdVisit
                    messagehashMap["isSeen"]=false
                    messagehashMap["url"]=url
                    messagehashMap["messageId"]=messageId

                    ref.child("Chats").child(messageId!!).setValue(messagehashMap)
                    progressBar.dismiss()
                }

            }

        }
    }

    private fun sendmessageToUser(senderId: String, receiverId: String?, message: String) {

        val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key
        val messagehashMap=HashMap<String,Any?>()
        messagehashMap["sender"]=senderId
        messagehashMap["message"]=message
        messagehashMap["receiver"]=receiverId
        messagehashMap["isSeen"]=false
        messagehashMap["url"]=""
        messagehashMap["messageId"]=messageKey
        reference.child("Chats").child(messageKey!!).setValue(messagehashMap).addOnCompleteListener {
            task->
            if(task.isSuccessful)
            {
                val chatListRefernece=FirebaseDatabase.getInstance().
                reference.child("ChatLists").child(firebaseUser!!.uid).child(userIdVisit!!)

                chatListRefernece.addValueEventListener(object : ValueEventListener
                {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(!snapshot.exists())
                        {
                            chatListRefernece.child("id").setValue(firebaseUser!!.uid)

                        }
                        val chatListReceiverRefernece=FirebaseDatabase.getInstance().
                        reference.child("ChatLists").child(userIdVisit!!).child(firebaseUser!!.uid)

                        chatListReceiverRefernece.child("id").setValue(firebaseUser!!.uid)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })



            }
        }

    }


}