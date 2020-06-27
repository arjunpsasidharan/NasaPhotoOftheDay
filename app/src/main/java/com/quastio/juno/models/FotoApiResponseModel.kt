package com.quastio.juno.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class FotoApiResponseModel(
    @SerializedName("copyright")
    @Expose
    private val copyright: String,
    @SerializedName("date")
    @Expose
    private val date: String,

    @SerializedName("explanation")
    @Expose
    val explanation: String,

    @SerializedName("hdurl")
    @Expose
    val hdurl: String,

    @SerializedName("media_type")
    @Expose
    val mediaType: String,

    @SerializedName("service_version")
    @Expose
    val serviceVersion: String,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("url")
    @Expose
    val url: String
)


