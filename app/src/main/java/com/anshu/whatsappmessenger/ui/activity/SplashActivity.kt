package com.anshu.whatsappmessenger.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.anshu.whatsappmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 1000 // 1 sec
    var firebaseUser: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
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