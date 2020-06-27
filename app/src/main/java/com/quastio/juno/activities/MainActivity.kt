package com.quastio.juno.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.quastio.juno.R
import com.quastio.juno.models.FotoApiResponseModel
import com.quastio.juno.models.ResultWrapper
import com.quastio.juno.utils.ThumbnailRetriever
import com.quastio.juno.viewmodels.FotoViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var fotoViewModel: FotoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fotoViewModel = ViewModelProvider(this).get(FotoViewModel::class.java)

        fotoViewModel.getTodaysDate().observe(this, Observer {
            fotoViewModel.updateLoader(false)
            it?.let {
                updateImageView(it)
            }
        })

        fotoViewModel.fotoWithDate.observe(this, Observer {
            it?.let {
                fotoViewModel.updateLoader(false)
                updateImageView(it)
            }
        })

        fotoViewModel.loaderData?.observe(this, Observer {
            if (it) {
                loader_group.visibility = View.VISIBLE
            } else {
                loader_group.visibility = View.GONE

            }
        })

        calender_iv.setOnClickListener {
            val c = Calendar.getInstance();
            val mYear = c.get(Calendar.YEAR);
            val mMonth = c.get(Calendar.MONTH);
            val mDay = c.get(Calendar.DAY_OF_MONTH);
            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, monthOfYear);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    val myFormat = "YYYY-MM-dd"

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    val date = sdf.format(c.time)
                    Log.e("debug", date)

                    fotoViewModel.getFotoForDate(date)

                }, mYear, mMonth, mDay
            )
//            datePickerDialog.datePicker.maxDate=(Date().time)

            datePickerDialog.show()
        }
    }

    private fun updateImageView(it: ResultWrapper<FotoApiResponseModel>) {
        when (it) {
            is ResultWrapper.Success -> {
                val response = it.data
                Toast.makeText(this, response.mediaType, Toast.LENGTH_SHORT).show()
                with(response) {
                    title?.let {
                        title_tv.text = title
                    }
                    var loadUrl:String?=null
                    url?.let {
                        loadUrl=url
                    }
                    hdurl?.let {
                        loadUrl=hdurl
                    }
                    loadUrl?.let {

                        when (mediaType) {
                            "video" -> {
                                action_iv.setImageResource(R.drawable.ic_baseline_play_circle_outline)
                                val videoId=ThumbnailRetriever.extractYTId(it)

                                videoId?.let {
                                    val requestOptions = RequestOptions()
                                    requestOptions.isMemoryCacheable
                                    Glide.with(this@MainActivity)
                                        .setDefaultRequestOptions(requestOptions)
                                        .load("https://img.youtube.com/vi/$videoId/0.jpg")
                                        .into(imageView)
                                }

                            }
                            else -> {
                                action_iv.setImageResource(R.drawable.ic_baseline_zoom_in)

                                Glide.with(this@MainActivity)
                                    .load(it)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(imageView)
                            }
                        }

                    }
                    explanation?.let {
                        description_tv.text = explanation
                    }
                }
            }
            is ResultWrapper.Error -> {
                Toast.makeText(this, "code  " + it.code + "  error " + it.error, Toast.LENGTH_SHORT)
                    .show()

            }
            ResultWrapper.NetworkError -> {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

 
}