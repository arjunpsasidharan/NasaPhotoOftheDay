package com.quastio.juno.models

sealed class ResultWrapper<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultWrapper<T>()
    data class Error(val code: Int? = null, val error: String? = null) : ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()

}