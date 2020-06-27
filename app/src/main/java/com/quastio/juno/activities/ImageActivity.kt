package com.quastio.juno.activities

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.quastio.juno.R
import com.quastio.juno.utils.CustomTouchListener
import kotlinx.android.synthetic.main.activity_image.*


class ImageActivity : AppCompatActivity() {

    private lateinit var imageView: SubsamplingScaleImageView
    private  val touchListener=CustomTouchListener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        imageView=findViewById(R.id.main_iv_1)
        val intent= intent
        intent?.let {
            val url=intent.getStringExtra(MainActivity.URL)

           if (url!=null&&url.isNotEmpty()){
               Glide.with(this).asBitmap()
                   .load(url)
                   .fitCenter()
                   .into(object :CustomTarget<Bitmap>(){
                       override fun onLoadCleared(placeholder: Drawable?) {
                       }

                       override fun onResourceReady(
                           resource: Bitmap,
                           transition: Transition<in Bitmap>?
                       ) {
                           main_iv_1.setImage(ImageSource.bitmap(resource))
                       }
                   })
           }
        }
//        main_iv.setOnTouchListener(touchListener)
    }




}