package com.tryamb.studentReside.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import com.tryamb.studentReside.R
import com.tryamb.studentReside.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {
 lateinit var binding:ActivityImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val img = intent.getStringExtra("img")

        if(img!=null) {
            Picasso.get().load(img.toUri()).into(binding.img)
        }
    }
}