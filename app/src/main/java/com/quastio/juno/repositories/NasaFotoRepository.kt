package com.quastio.juno.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.quastio.juno.models.FotoApiResponseModel
import com.quastio.juno.models.ResultWrapper
import com.quastio.juno.restclient.RestClient
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.HttpException
import java.io.IOException

object NasaFotoRepository {
    var fotoJob:CompletableJob?=null
    var fotoWithDateJob:CompletableJob?=null

    fun<T> getTodaysFoto():MutableLiveData<ResultWrapper<FotoApiResponseModel>>{

        fotoJob= Job()

        return object :MutableLiveData<ResultWrapper<FotoApiResponseModel>>(){
            override fun onActive() {
                super.onActive()
                fotoJob?.let {
                    CoroutineScope(IO+ it).launch {
                        try {
                            val data=    RestClient.nasaApiService.getTodaysFoto("DEMO_KEY")
                            withContext(Main){
                                value=ResultWrapper.Success(data)
                                it.complete()
                            }
                        }catch (throwable:Throwable){
                           when(throwable){
                               is IOException->{
                                   withContext(Main) {
                                       value = ResultWrapper.NetworkError
                                       it.complete()
                                   }

                               }
                               is HttpException->{
                                   withContext(Main) {
                                       value = ResultWrapper.Error(
                                           throwable.code(),
                                           throwable.message()
                                       )
                                       it.complete()
                                   }
                               }
                               else->{
                                   withContext(Main) {
                                       value = ResultWrapper.NetworkError
                                       it.complete()
                                   }

                               }
                           }
                        }

                    }
                }
            }
        }

    }
    fun<T> getFotoWithDate(date:String):LiveData<ResultWrapper<FotoApiResponseModel>>{

        fotoWithDateJob= Job()

        return object :LiveData<ResultWrapper<FotoApiResponseModel>>(){
            override fun onActive() {
                super.onActive()
                fotoWithDateJob?.let {
                    CoroutineScope(IO+ it).launch {
                      try {
                        val data = RestClient.nasaApiService.getFotoWithDate("DEMO_KEY", date)
                        withContext(Main) {
                            postValue( ResultWrapper.Success(data))
                            it.complete()
                        }
                    }catch (throwable:Throwable){
                            when(throwable){
                                is IOException->{
                                    withContext(Main){

                                    value=ResultWrapper.NetworkError
                                    it.complete()
                                    }


                                }
                                is HttpException->{
                                    withContext(Main) {
                                        value = ResultWrapper.Error(
                                            throwable.code(),
                                            throwable.message()
                                        )
                                        it.complete()
                                    }
                                }
                                else->{
                                    withContext(Main) {
                                        value = ResultWrapper.NetworkError
                                        it.complete()
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

    }
    fun cancelJob(){
        fotoWithDateJob?.let {
            it.cancel()
        }
        fotoJob?.let {
            it.cancel()
        }
    }
}