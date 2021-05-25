package com.example.musichead.api


import com.example.musichead.models.Places
import com.example.musichead.models.ResponseBase
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/ws/2/place/?fmt=json")
    suspend fun getPlaces(@Query("query") aParam: String?,@Query("limit") limit: Int?): ResponseBase
}