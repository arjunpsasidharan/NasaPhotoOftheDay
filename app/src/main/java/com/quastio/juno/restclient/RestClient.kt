package com.quastio.juno.restclient

import com.google.gson.GsonBuilder
import com.quastio.juno.models.FotoApiResponseModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object RestClient {

    private const val BASE_URL="https://api.nasa.gov/planetary/"

    private val defaultHttpClient:OkHttpClient by lazy {
    OkHttpClient.Builder()
        .connectTimeout(20,TimeUnit.SECONDS)
        .readTimeout(10,TimeUnit.SECONDS)
        .writeTimeout(10,TimeUnit.SECONDS)
        .build()
    }

    private val retrofitBuilder:Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(defaultHttpClient)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create()))
    }

    val nasaApiService:NasaApiService by lazy {
        retrofitBuilder.build().create(NasaApiService::class.java)
    }


    interface NasaApiService {
        @GET("apod")
        suspend fun getTodaysFoto(@Query("api_key") api:String ):FotoApiResponseModel

        @GET("apod")
        suspend fun getFotoWithDate(@Query("api_key") api:String ,@Query("date")date:String):FotoApiResponseModel

    }
}


//
//apod?api_key=DEMO_KEY
//https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&date=YYYY-MM-DD