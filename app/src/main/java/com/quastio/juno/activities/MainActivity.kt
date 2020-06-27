package com.quastio.juno.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ScrollView
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
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val VIDEO="video"
        private const val IMAGE="image"
        const val URL="url"
    }
    lateinit var fotoViewModel: FotoViewModel
    private var loadUrl:String?=null
    private var videoId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            datePickerDialog.datePicker.maxDate=(Date().time)

            datePickerDialog.show()
        }
        action_iv.setOnClickListener {
            if (action_iv.tag==IMAGE){
                val intent=Intent(this,ImageActivity::class.java)
                intent.putExtra(URL,loadUrl)
                startActivity(intent)
            }else if (action_iv.tag== VIDEO){
                videoId?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
                    intent.putExtra("VIDEO_ID", videoId)
                    startActivity(intent)
                }

            }
        }
    }

    private fun updateImageView(it: ResultWrapper<FotoApiResponseModel>) {
        when (it) {
            is ResultWrapper.Success -> {
                val response = it.data
                with(response) {
                    title?.let {
                        title_tv.text = title
                    }
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
                                action_iv.tag = VIDEO
                                 videoId=ThumbnailRetriever.extractYTId(it)

                                videoId?.let {
                                    val requestOptions = RequestOptions()
                                    requestOptions.isMemoryCacheable
                                    Glide.with(this@MainActivity)
                                        .setDefaultRequestOptions(requestOptions)
                                        .load("https://img.youtube.com/vi/$videoId/0.jpg")
                                        .into(main_iv)
                                }

                            }
                            else -> {
                                action_iv.setImageResource(R.drawable.ic_baseline_zoom_in)
                                action_iv.tag = IMAGE

                                Glide.with(this@MainActivity)
                                    .load(it)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(main_iv)
                            }
                        }

                    }
                    explanation?.let {
                        description_tv.text = explanation
                        scroll_view.fullScroll(ScrollView.FOCUS_UP)
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