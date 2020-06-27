package com.quastio.juno.activities

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.quastio.juno.R
import com.quastio.juno.utils.CustomTouchListener
import kotlinx.android.synthetic.main.activity_main.*


class ImageActivity : AppCompatActivity() {


    private  val touchListener=CustomTouchListener()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val intent= intent
        intent?.let {
            val url=intent.getStringExtra(MainActivity.URL)

           if (url!=null&&url.isNotEmpty()){
               Glide.with(this)
                   .load(url)
                   .into(main_iv)
           }
        }
        main_iv.setOnTouchListener(touchListener)
    }


}