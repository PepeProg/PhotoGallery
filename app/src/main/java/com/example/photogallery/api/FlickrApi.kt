package com.example.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

interface FlickrApi{
    @GET("/")   //using base URL-address
    fun fetchContents(): Call<String>   //type parameter is a type of response's deserializing

    @GET("services/rest?method=flickr.interestingness.getList" +
            "&api_key=98254a27f1cb6135e49d5bb32acd7dc9" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s")
    fun fetchPhotos(): Call<FlickrResponse>
}