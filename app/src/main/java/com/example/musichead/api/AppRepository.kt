package com.example.musichead.api

import android.util.Log
import com.example.musichead.models.Places
import com.example.musichead.models.ResponseBase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class AppRepository @Inject constructor(val api : ApiService){

    fun getPlacesData(place : String,limit : Int): Flow<ResponseBase> {
        return flow {
            val response= api.getPlaces(place,limit)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}