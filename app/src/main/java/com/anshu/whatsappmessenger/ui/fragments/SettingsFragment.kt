package com.anshu.whatsappmessenger.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.anshu.whatsappmessenger.R
import com.anshu.whatsappmessenger.model.Users
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.user_item_search_layout.view.*

class SettingsFragment : Fragment() {

    var userReference:DatabaseReference?=null
    var firebaseUser:FirebaseUser?= null
    var REQUEST_CODE=42
    private var imageUri: Uri?=null
    private var storageRef: StorageReference?=null

    private var coverChecker:String?=""
    private var socialChecker:String?=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser=FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef=FirebaseStorage.getInstance().reference.child("User Images")
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

        view.profile_image_settings.setOnClickListener {
            pickImage()
        }
        view.cover_image.setOnClickListener {
            coverChecker="cover"
            pickImage()
        }
        view.set_facebook.setOnClickListener {
            socialChecker="facebook"
            setSocialLinks()
        }
        view.set_instagram.setOnClickListener {
            socialChecker="instagram"
            setSocialLinks()
        }
        view.set_website.setOnClickListener {
            socialChecker="website"
            setSocialLinks()
        }


        return view
    }

    private fun setSocialLinks() {
        val builder:AlertDialog.Builder= AlertDialog.Builder(requireContext(),R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if(socialChecker=="website")
        {
            builder.setMessage("Write url")

        }
        else
        {
            builder.setMessage("Write username")

        }
        val editText=EditText(context)
        if(socialChecker=="website")
        {
            editText.hint="www.google.com"

        }
        else
        {
            editText.hint="abc123"

        }
        builder.setView(editText)
        builder.setPositiveButton("Create",DialogInterface.OnClickListener{
            dialog,which->
            val str=editText.toString()
            if(str=="")
            {
                Toast.makeText(context,"Please write something",Toast.LENGTH_SHORT).show()

            }
            else
            {
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {

    val mapSocial=HashMap<String,Any>()
        when(socialChecker)
        {
            "facebook"->
            {
                mapSocial["facebook"]="https://m.facebook.com/$str"
            }
            "instagram"->
            {
                mapSocial["instagram"]="https://m.instagram.com/$str"
            }
            "website"->
            {
                mapSocial["website"]="https://$str"
            }
        }

        userReference!!.updateChildren(mapSocial).addOnCompleteListener{
            task->
            if(task.isSuccessful)
            {
                Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun pickImage() {
        val intent = Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==REQUEST_CODE&& resultCode== Activity.RESULT_OK&&data!!.data!=null)
        {
            imageUri=data.data
            Toast.makeText(context,"Uploading......",Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("Image is uploading....")
        progressBar.show()
        if(imageUri!=null)
        {
            val fileRef=storageRef!!.child(System.currentTimeMillis().toString()+".jpg")
            var uploadTask:StorageTask<*>
            uploadTask=fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{task->
                if(!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{task->
                if(task.isSuccessful)
                {
                    var downloadUrl =task.result
                    val url = downloadUrl.toString()
                    if(coverChecker=="cover")
                    {
                            val mapCoverImage=HashMap<String,Any>()
                            mapCoverImage["cover"]=url
                            userReference!!.updateChildren(mapCoverImage)
                            coverChecker=""


                    }
                    else
                    {
                        val mapProfileImage=HashMap<String,Any>()
                        mapProfileImage["profile"]=url
                        userReference!!.updateChildren(mapProfileImage)
                        coverChecker=""
                    }
                    progressBar.dismiss()
                }

            }
        }
    }


}