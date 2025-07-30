package com.midas.whatsapp.util



sealed class CustomResult<out T> {
    data class Success<out T>(val data: T): CustomResult<T>()
    data class Failure(val exception: Exception): CustomResult<Nothing>()

    companion object{
        fun <T> success(data: T) = Success(data)
        fun failure(exception: Exception) = Failure(exception)
    }

}