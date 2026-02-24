package com.practicum.myapp.api

import com.practicum.myapp.ItunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("search?entity=song")
    fun search(@Query("term") text: String): Call<ItunesResponse>
}
