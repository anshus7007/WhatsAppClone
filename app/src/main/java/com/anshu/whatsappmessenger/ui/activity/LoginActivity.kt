package com.anshu.whatsappmessenger.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.anshu.whatsappmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    var firebaseUser: FirebaseUser?=null
    lateinit var tvRegisterNewUser:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth= FirebaseAuth.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Login Yourself"
        tvRegisterNewUser=findViewById(R.id.tvRegisterNewUser)
        tvRegisterNewUser.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val email: String = etEmailLogin.text.toString()
        val password: String = etPasswordLogin.text.toString()


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    finish()

                }
                else
                {
                    Toast.makeText(this,"logon error"+task.exception!!.message,Toast.LENGTH_SHORT).show()

                }
            }
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