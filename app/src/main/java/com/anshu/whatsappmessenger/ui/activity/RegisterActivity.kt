package com.anshu.whatsappmessenger.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.anshu.whatsappmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        mAuth= FirebaseAuth.getInstance()
        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username: String = etUserNameRegister.text.toString()
        val email: String = etEmailRegister.text.toString()
        val password: String= etPasswordRegister.text.toString()


        if(username.isEmpty()||email.isEmpty()||password.isEmpty())
        {
            Toast.makeText(this,"Please fill all the details",Toast.LENGTH_SHORT).show()
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                task->
                if(task.isSuccessful)
                {
                    firebaseUserID=mAuth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    val userHashMap= HashMap<String,Any>()
                    userHashMap["uid"]=firebaseUserID
                    userHashMap["username"]=username
                    userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/whatsappmessenger-db0f6.appspot.com/o/profile.png?alt=media&token=95341e7e-2cf0-4c98-9deb-faaecbeeb0ec"
                    userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/whatsappmessenger-db0f6.appspot.com/o/cover.png?alt=media&token=d5a8f605-4571-4ae9-82db-5137c45c1e2c"
                    userHashMap["status"]="offline"
                    userHashMap["search"]=username.toLowerCase()
                    userHashMap["facebook"]="https://m.facebook.com"
                    userHashMap["instagram"]="https://m.instagram.com"
                    userHashMap["website"]="https://www.google.com"

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener{ task->
                            if(task.isSuccessful)
                            {
                                startActivity(Intent(this,MainActivity::class.java))
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                finish()
                            }
                            else
                            {
                                Toast.makeText(this,"Error"+task.exception!!.message,Toast.LENGTH_SHORT).show()

                            }
                    }

                }
                else
                {
                    Toast.makeText(this,"Unkone error"+task.exception!!.message,Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}