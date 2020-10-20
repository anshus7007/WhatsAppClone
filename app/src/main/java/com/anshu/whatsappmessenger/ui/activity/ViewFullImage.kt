package com.anshu.whatsappmessenger.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.anshu.whatsappmessenger.R
import com.squareup.picasso.Picasso

class ViewFullImage : AppCompatActivity() {
    private var imageViewer:ImageView?=null
    private var imageUrl:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        imageUrl= intent.getStringExtra("url").toString()
        imageViewer=findViewById(R.id.fullImage)
        Picasso.get().load(imageUrl).into(imageViewer)
    }
}