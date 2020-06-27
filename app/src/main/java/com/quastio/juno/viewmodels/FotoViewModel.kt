package com.quastio.juno.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.quastio.juno.models.FotoApiResponseModel
import com.quastio.juno.models.ResultWrapper
import com.quastio.juno.repositories.NasaFotoRepository

class FotoViewModel :ViewModel() {

    private val _dateString=MutableLiveData<String>()
    private val _isLoading=MutableLiveData<Boolean>()
    private var _fotoData:MutableLiveData<ResultWrapper<FotoApiResponseModel>>?=null
    init {
        _isLoading.value=true
        _fotoData=NasaFotoRepository.getTodaysFoto<FotoApiResponseModel>()

    }
    val loaderData:LiveData<Boolean>?=_isLoading
    val fotoWithDate=Transformations
        .switchMap(_dateString){
            _isLoading.postValue(true)
            NasaFotoRepository.getFotoWithDate<FotoApiResponseModel>(it)
        }
   fun getFotoForDate(date:String){
       if (_dateString.value==date){
           return
       }

       _dateString.value=date
   }

    fun getTodaysDate():LiveData<ResultWrapper<FotoApiResponseModel>>{
        if (_fotoData!=null)
            return _fotoData as LiveData<ResultWrapper<FotoApiResponseModel>>
        else {
            _isLoading.postValue(true)
            return NasaFotoRepository.getTodaysFoto<FotoApiResponseModel>()
        }
    }

    fun updateLoader(boolean: Boolean){
        _isLoading.postValue(boolean)
    }
}