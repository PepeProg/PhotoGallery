package com.example.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

interface FlickrApi{
    @GET("/")   //using base URL-address
    fun fetchContents(): Call<String>   //type parameter is a type of response's deserializing
}