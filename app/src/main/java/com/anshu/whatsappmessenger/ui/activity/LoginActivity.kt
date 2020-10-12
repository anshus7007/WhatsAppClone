package com.anshu.whatsappmessenger.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.anshu.whatsappmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser?=null
    lateinit var tvRegisterNewUser:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Login Yourself"
        tvRegisterNewUser=findViewById(R.id.tvRegisterNewUser)
        tvRegisterNewUser.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        firebaseUser= FirebaseAuth.getInstance().currentUser

        if(firebaseUser!=null)
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
}